/**
 * pdbconverter - Convert Palm PDB files into more common formats
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://pdbconverter.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.pdbconverter.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXDatePicker;
import org.shredzone.pdbconverter.CalendarFactory;
import org.shredzone.pdbconverter.ConverterRegister;
import org.shredzone.pdbconverter.handler.ExportHandler;
import org.shredzone.pdbconverter.handler.ExportOptions;

/**
 * A very simple Swing panel for file conversion. It allows the selection of the converter
 * along with the input and output file, and offers a button to convert the choice.
 *
 * @author Richard "Shred" Körber
 */
public class ConverterPanel extends JPanel {
    private static final long serialVersionUID = 779442646747161290L;

    private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("messages");

    private CalendarFactory cf = CalendarFactory.getInstance();
    private JComboBox<ExportHandler> jcbMode;
    private JTextField jtInfile;
    private JTextField jtOutfile;
    private JCheckBox jcSplit;
    private JXDatePicker jxdpFrom;
    private JXDatePicker jxdpThru;
    private JButton jbConvert;
    private Set<JLabel> labels = new HashSet<JLabel>();

    /**
     * Creates a new {@link ConverterPanel}.
     */
    public ConverterPanel() {
        build();
    }

    /**
     * Builds the GUI.
     */
    private void build() {
        setLayout(new GridLayout(0, 1, 0, 2));
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        jcbMode = buildModeSelector(this, RESOURCE.getString("label.converter"));
        jtInfile = buildFileSelector(this, RESOURCE.getString("label.infile"), false);
        jtOutfile = buildFileSelector(this, RESOURCE.getString("label.outfile"), true);

        JPanel jpSplit = new JPanel(new BorderLayout());
        {
            JLabel jlSplit = new JLabel(" ");
            jpSplit.add(jlSplit, BorderLayout.LINE_START);
            labels.add(jlSplit);

            jcSplit = new JCheckBox(RESOURCE.getString("label.split"));
            jcSplit.setToolTipText(RESOURCE.getString("label.split.tt"));
            jpSplit.add(jcSplit, BorderLayout.CENTER);
        }
        add(jpSplit);

        JPanel jpFrom = new JPanel(new BorderLayout());
        {
            JLabel jlFrom = new JLabel(RESOURCE.getString("label.from"));
            jpFrom.add(jlFrom, BorderLayout.LINE_START);
            labels.add(jlFrom);

            jxdpFrom = new JXDatePicker();
            jxdpFrom.setToolTipText(RESOURCE.getString("label.from.tt"));
            jpFrom.add(jxdpFrom, BorderLayout.CENTER);
        }
        add(jpFrom);

        JPanel jpThru = new JPanel(new BorderLayout());
        {
            JLabel jlThru = new JLabel(RESOURCE.getString("label.thru"));
            jpThru.add(jlThru, BorderLayout.LINE_START);
            labels.add(jlThru);

            jxdpThru = new JXDatePicker();
            jxdpThru.setToolTipText(RESOURCE.getString("label.thru.tt"));
            jpThru.add(jxdpThru, BorderLayout.CENTER);
        }
        add(jpThru);

        jbConvert = new JButton(RESOURCE.getString("label.convert"));
        jbConvert.addActionListener(new ConvertActionListener());
        add(jbConvert);

        arrangeLabels();
    }

    /**
     * Builds a mode selector. It is a {@link JComboBox} that shows all
     * registered {@link ExportHandler}.
     *
     * @param parent
     *            parent component to add the selector to
     * @param label
     *            Label text
     * @return {@link JComboBox} that was created
     */
    private JComboBox<ExportHandler> buildModeSelector(JPanel parent, String label) {
        JPanel jpOuter = new JPanel(new BorderLayout());

        JLabel jlLabel = new JLabel(label);
        jpOuter.add(jlLabel, BorderLayout.LINE_START);
        labels.add(jlLabel);

        JComboBox<ExportHandler> jcbComboBox = new JComboBox<ExportHandler>(ConverterRegister.getHandlers());
        jcbComboBox.setRenderer(new ModeListCellRenderer());
        jpOuter.add(jcbComboBox, BorderLayout.CENTER);

        parent.add(jpOuter);

        jlLabel.setLabelFor(jcbComboBox);

        return jcbComboBox;
    }

    /**
     * Builds a file selector. It is a text field and a button that opens a file
     * chooser.
     *
     * @param parent
     *            parent component to add the selector to
     * @param label
     *            Label text
     * @param saveMode
     *            {@code true} if the selected file is to be written, {@code
     *            false} if it is to be read
     * @return {@link JTextField} that contains the selected file name
     */
    private JTextField buildFileSelector(JPanel parent, String label, boolean saveMode) {
        JPanel jpOuter = new JPanel(new BorderLayout());

        JLabel jlLabel = new JLabel(label);
        jpOuter.add(jlLabel, BorderLayout.LINE_START);
        labels.add(jlLabel);

        JTextField jtFileName = new JTextField();
        jpOuter.add(jtFileName, BorderLayout.CENTER);

        JButton jbSelect = new JButton("<");
        jbSelect.setMargin(new Insets(0, 2, 0, 2));
        jbSelect.addActionListener(new FileSelectActionListener(jtFileName, saveMode));
        jpOuter.add(jbSelect, BorderLayout.LINE_END);

        parent.add(jpOuter);

        jlLabel.setLabelFor(jbSelect);

        return jtFileName;
    }

    /**
     * Arranges all labels so they consume the same width.
     */
    private void arrangeLabels() {
        int max = 0;
        for (JLabel label : labels) {
            max = Math.max(max, label.getMinimumSize().width);
        }
        max += 5;

        for (JLabel label : labels) {
            Dimension dim = new Dimension(max, label.getMinimumSize().height);
            label.setMinimumSize(dim);
            label.setPreferredSize(dim);
            label.invalidate();
        }
    }

    /**
     * Listener for the convert button. It invokes the selected {@link ExportHandler}
     * and informs the user about the result.
     */
    public class ConvertActionListener implements ActionListener {
        @Override
        @SuppressWarnings("synthetic-access")
        public void actionPerformed(ActionEvent e) {
            ExportHandler handler = (ExportHandler) jcbMode.getSelectedItem();
            if (handler != null) {
                try {
                    File infile = new File(jtInfile.getText());
                    File outfile = new File(jtOutfile.getText());

                    ExportOptions options = new ExportOptions();
                    options.setSplit(jcSplit.isSelected());

                    if (jxdpFrom.getDate() != null) {
                        Calendar fromCal = cf.create();
                        fromCal.setTime(jxdpFrom.getDate());
                        options.setFrom(fromCal);
                    }

                    if (jxdpThru.getDate() != null) {
                        // Option value is exclusive, so add 1 day!
                        Calendar thruCal = cf.create();
                        thruCal.setTime(jxdpThru.getDate());
                        thruCal.add(Calendar.DAY_OF_YEAR, 1);
                        options.setUntil(thruCal);
                    } else {
                        options.setUntil(null);
                    }

                    handler.export(infile, outfile, options);

                    JOptionPane.showMessageDialog(
                            ConverterPanel.this,
                            RESOURCE.getString("convert.success"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            ConverterPanel.this,
                            RESOURCE.getString("convert.failed") + "\n" + ex.getMessage(),
                            RESOURCE.getString("convert.failed.title"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Listener for the file selection button. It opens a file chooser and copies the
     * selected file name into the {@link JTextField}.
     */
    public static class FileSelectActionListener implements ActionListener {
        private final JTextField target;
        private final boolean saveMode;
        private File currentDir = null;

        /**
         * Creates a new {@link FileSelectActionListener}.
         *
         * @param target
         *            Target {@link JTextField} where the selected file name is
         *            set
         * @param saveMode
         *            {@code true} for save mode file chooser
         */
        public FileSelectActionListener(JTextField target, boolean saveMode) {
            this.target = target;
            this.saveMode = saveMode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Component src = (Component) e.getSource();

            JFileChooser jChooser = new JFileChooser(currentDir);
            jChooser.setMultiSelectionEnabled(false);

            if (!saveMode) {
                jChooser.setFileFilter(new FileFilter() {
                    @SuppressWarnings("synthetic-access")
                    @Override
                    public String getDescription() {
                        return RESOURCE.getString("pdbfilter");
                    }

                    @Override
                    public boolean accept(File f) {
                        if (f.isHidden()) return false;
                        if (f.isDirectory()) return true;
                        if (f.getName().toLowerCase().endsWith(".pdb")) return true;
                        if (f.getName().toLowerCase().endsWith(".mdb")) return true;
                        return false;
                    }
                });
            }

            int choice = (saveMode ? jChooser.showSaveDialog(src) : jChooser.showOpenDialog(src));
            if (choice == JFileChooser.APPROVE_OPTION) {
                currentDir = jChooser.getCurrentDirectory();
                target.setText(jChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    /**
     * A {@link DefaultListCellRenderer} that shows the name and description of a
     * {@link ExportHandler}.
     */
    public static class ModeListCellRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 7351366482641122918L;

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ExportHandler handler = (ExportHandler) value;
            StringBuilder sb = new StringBuilder();
            sb.append("<html>").append(handler.getName())
                .append("<font size=\"-2\" color=\"#707070\"> - ")
                .append(handler.getDescription());

            return super.getListCellRendererComponent(list, sb.toString(), index, isSelected, cellHasFocus);
        }
    }

}
