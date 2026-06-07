package org.zaproxy.zap.extension.ollama;

import org.parosproxy.paros.view.AbstractParamPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class OllamaOptionsPanel extends AbstractParamPanel {

    private final ExtensionOllama extension;
    private final JTextField baseUrlField;
    private final JComboBox<String> modelCombo;
    private final JTextField fallbackModelsField;
    private final JSpinner timeoutSpinner;
    private final JComboBox<String> riskCombo;
    private final JTextField outputDirField;
    private final JCheckBox enrichCb, fpCb, streamingCb, multiCb, triageCb, cweCb, reportCb;
    private final JSpinner maxReqSpinner;
    private final JTextArea promptAnalyze, promptFp, promptExplain, templates;

    public OllamaOptionsPanel(ExtensionOllama ext) {
        this.extension = ext;
        OllamaOptions o = ext.getOptions();

        baseUrlField = new JTextField(o.getBaseUrl(), 30);
        modelCombo = new JComboBox<>(); modelCombo.setEditable(true); modelCombo.addItem(o.getModel());
        fallbackModelsField = new JTextField(o.getFallbackModels(), 30);
        timeoutSpinner = new JSpinner(new SpinnerNumberModel(o.getTimeoutSeconds(), 10, 600, 10));
        riskCombo = new JComboBox<>(new String[]{"Low","Medium","High"}); riskCombo.setSelectedItem(o.getRiskFilter());
        outputDirField = new JTextField(o.getOutputDir(), 30);
        enrichCb = new JCheckBox("Enrich alerts with AI analysis", o.isEnrichAlerts());
        fpCb = new JCheckBox("Validate false positives", o.isValidateFalsePositives());
        streamingCb = new JCheckBox("Enable streaming responses", o.isStreamingEnabled());
        multiCb = new JCheckBox("Enable multi-model fallback", o.isMultiModelEnabled());
        triageCb = new JCheckBox("Enable auto-triage", o.isAutoTriageEnabled());
        cweCb = new JCheckBox("Enable CWE mapping", o.isCweMappingEnabled());
        reportCb = new JCheckBox("Enable auto-report generation", o.isAutoReportEnabled());
        maxReqSpinner = new JSpinner(new SpinnerNumberModel(o.getMaxRequestLength(), 1000, 50000, 1000));
        promptAnalyze = area(o.getSystemPromptAnalyze()); promptFp = area(o.getSystemPromptValidateFP());
        promptExplain = area(o.getSystemPromptExplain());
        templates = area(o.getPromptTemplates()); templates.setToolTipText("KEY=prompt, one per line");
        buildUi();
    }

    private JTextArea area(String text) {
        JTextArea a = new JTextArea(text, 3, 40);
        a.setLineWrap(true); a.setWrapStyleWord(true); return a;
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(4,4,4,4);

        int y=0;
        JButton testBtn = new JButton("Test"); testBtn.addActionListener(e -> testConnection());
        JButton refBtn = new JButton("Refresh"); refBtn.addActionListener(e -> refreshModels());
        addRow(form,y++,"Ollama URL:",panel(baseUrlField,testBtn,refBtn),c);
        addRow(form,y++,"Model:",panel(modelCombo),c);
        addRow(form,y++,"Fallback Models:",panel(fallbackModelsField),c);
        addRow(form,y++,"Timeout (sec):",panel(timeoutSpinner),c);
        addRow(form,y++,"Risk Filter:",panel(riskCombo),c);
        addRow(form,y++,"Output Dir:",panel(outputDirField),c);
        addRow(form,y++,"Max Request Len:",panel(maxReqSpinner),c);

        JPanel feats = new JPanel(new GridBagLayout());
        feats.setBorder(new TitledBorder("Enhanced Features"));
        GridBagConstraints fc = new GridBagConstraints(); fc.fill=GridBagConstraints.HORIZONTAL; fc.weightx=1;
        fc.gridy=0;feats.add(enrichCb,fc); fc.gridy=1;feats.add(fpCb,fc); fc.gridy=2;feats.add(streamingCb,fc);
        fc.gridy=3;feats.add(multiCb,fc); fc.gridy=4;feats.add(triageCb,fc); fc.gridy=5;feats.add(cweCb,fc); fc.gridy=6;feats.add(reportCb,fc);
        c.gridy=y++;c.gridx=0;c.gridwidth=2;form.add(feats,c);

        JPanel prompts = new JPanel(new GridBagLayout());
        prompts.setBorder(new TitledBorder("System Prompts"));
        GridBagConstraints pc = new GridBagConstraints(); pc.fill=GridBagConstraints.HORIZONTAL; pc.weightx=1;
        addLabeled(prompts,pc,"Analyze:",promptAnalyze); addLabeled(prompts,pc,"Validate FP:",promptFp); addLabeled(prompts,pc,"Explain:",promptExplain);
        c.gridy=y++;c.gridx=0;c.gridwidth=2;form.add(prompts,c);

        JPanel tmpl = new JPanel(new BorderLayout()); tmpl.setBorder(new TitledBorder("Prompt Templates"));
        tmpl.add(new JScrollPane(templates),BorderLayout.CENTER);
        c.gridy=y++;c.gridx=0;c.gridwidth=2;form.add(tmpl,c);

        add(new JScrollPane(form),BorderLayout.CENTER);
    }

    private void addRow(JPanel f, int y, String l, JPanel p, GridBagConstraints c) {
        c.gridy=y; c.gridx=0; c.weightx=0; f.add(new JLabel(l),c);
        c.gridx=1; c.weightx=1; f.add(p,c);
    }
    private void addLabeled(JPanel p, GridBagConstraints c, String label, JComponent comp) {
        c.gridy++; p.add(new JLabel(label),c); c.gridy++; p.add(new JScrollPane(comp),c);
    }
    private JPanel panel(java.awt.Component... comps) {
        JPanel p = new JPanel(); for (java.awt.Component c : comps) p.add(c); return p;
    }

    @Override public void initParam(Object o) {}
    @Override public void validateParam(Object o) {}
    @Override
    public void saveParam(Object o) {
        OllamaOptions opts = extension.getOptions();
        opts.setBaseUrl(baseUrlField.getText().trim());
        opts.setModel(modelCombo.getSelectedItem() != null ? modelCombo.getSelectedItem().toString().trim() : "llama3.2:3b");
        opts.setFallbackModels(fallbackModelsField.getText().trim());
        opts.setTimeoutSeconds((Integer)timeoutSpinner.getValue());
        opts.setRiskFilter((String)riskCombo.getSelectedItem());
        opts.setOutputDir(outputDirField.getText().trim());
        opts.setEnrichAlerts(enrichCb.isSelected());
        opts.setValidateFalsePositives(fpCb.isSelected());
        opts.setStreamingEnabled(streamingCb.isSelected());
        opts.setMultiModelEnabled(multiCb.isSelected());
        opts.setAutoTriageEnabled(triageCb.isSelected());
        opts.setCweMappingEnabled(cweCb.isSelected());
        opts.setAutoReportEnabled(reportCb.isSelected());
        opts.setMaxRequestLength((Integer)maxReqSpinner.getValue());
        opts.setSystemPromptAnalyze(promptAnalyze.getText());
        opts.setSystemPromptValidateFP(promptFp.getText());
        opts.setSystemPromptExplain(promptExplain.getText());
        opts.setPromptTemplates(templates.getText());
    }
    @Override public String getHelpIndex() { return "ollama"; }

    private void testConnection() {
        saveParam(null);
        new Thread(() -> {
            boolean ok = extension.getOllamaService().healthCheck();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                ok ? "Connection OK!" : "Cannot connect to " + baseUrlField.getText(),
                "Ollama Connection", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
        }).start();
    }
    private void refreshModels() {
        new Thread(() -> {
            try {
                java.util.List<String> m = extension.getOllamaService().listModels();
                SwingUtilities.invokeLater(() -> { modelCombo.removeAllItems(); for (String s : m) modelCombo.addItem(s); });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,"Failed: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
