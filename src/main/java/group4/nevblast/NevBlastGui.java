/*
 * 
 This file is part of NEVBLAST.

 NEVBLAST is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 NEVBLAST is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.



 You should have received a copy of the GNU General Public License
 along with NEVBLAST.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
/**
 * @NevBlastGui.java This class is the main brain behind NEVBLAST, this is the
 * program that calls all of the moving pieces. When the Submit Button is
 * clicked the method btnSubmitAction is called which then feeds all the input
 * to the method sanitizeInput to be sanitized. Inside sanitizeInput the
 * signature sequences are verified and created (see section for logic). After
 * sanitizeInput it is returned to btnSubmitAction where it calls the class
 * BlastQuery to query BLAST. The output returned from BlastQuery is then send
 * to the method CreateOutput. Here the final screen is prepared, by creating
 * the three windows: ResultsTable,Graph,TextPane.
 *
 *
 * The menu system is built inside the constructor by calling makeMenu
 */
package group4.nevblast;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 *
 * @author Matthew Zygowicz - Ziggy @coauther Tony Krump
 */
public class NevBlastGui extends MasterProgram {

    /**
     * Creates new form NevBlastGui - defaults filled in
     *
     */
    public NevBlastGui() throws IOException {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                destroyGui();
            }
        });
        count++;
        makeMenu();
        initComponents();
        //parse file here
        parseTaxonomyFile();
        
        jLabel10.setVisible(false);
        txt_eValue.setText(".1");
        txt_fastaSequence.setText(">gi|19918470|gb|AAM07687.1| efflux system transcriptional regulator, ArsR family [Methanosarcina acetivorans C2A]\n"
                + "MQEKCDRVNPEQIENLLQKVPDPEYITRMSAVFQALQSDTRLKILFLLRQKEMCVCELEQALEVTQSAVS\n"
                + "HGLRTLRQLDLVRVRREGKFTVYYIADEHVRTLIEMCLEHVEEKI");
        txt_signature1.setText("S 67 S 70 H 71 L 76 Y 93");
        txt_signature2.setText("C 5 C 54 C56");
        txt_numberOfResults.setText("1000");
        txt_queryName.setText("Test Query");
        

    }
    private void parseTaxonomyFile() throws FileNotFoundException, IOException{
        File file = new File("taxonomy.txt");
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        //ArrayList<String> autocompleteDictionary = new ArrayList<String>();
        EventList taxonomy = new BasicEventList();
//        Vector<String> taxonomy = null;
        String line;
        while ((line = br.readLine()) != null) {
//            taxonomy.add(line);
            taxonomy.add(line);
            txt_entrezQuery.addItem(line);
            
        }
  //      Configurator.enableAutoCompleteion(txt_entrezQuery);
        br.close();
        AutoCompleteSupport autoComplete = AutoCompleteSupport.install(txt_entrezQuery, taxonomy);
  //      Autocomplete autoComplete = new Autocomplete(txt_entrezQuery, autocompleteDictionary);
//        txt_entrezQuery.getDocument().addDocumentListener(autoComplete);
//        txt_entrezQuery.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "commit");
//        txt_entrezQuery.getActionMap().put("commit", autoComplete.new CommitAction());
        
    }
    private void makeMenu(){
        JMenuBar menubar = new JMenuBar();
        JMenu file = makeMenuFile();
        JMenu matrix = makeMenuMatrix();
        JMenu database = makeMenuDatabase();
        JMenu program = makeMenuProgram();
      
        
        menubar.add(file);
        menubar.add(matrix);
      //  menubar.add(program);
        menubar.add(database);
        
        setJMenuBar(menubar);
        
        //setdefaults for menus
        globalMatrix = "blosum62";
        globalBlastDatabase = "nr";
        globalBlastProgram = "blastp";
    }
    private JMenu makeMenuProgram(){
         final JMenu program = new JMenu("BLAST Program");
         //set default
        final JCheckBoxMenuItem cbMenuItem1;
        cbMenuItem1 = new JCheckBoxMenuItem("blastp");
        cbMenuItem1.setMnemonic(KeyEvent.VK_C);
        cbMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "blastp";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem1.setSelected(true);
                
            }
        });
        cbMenuItem1.setSelected(true);
        
        final JCheckBoxMenuItem cbMenuItem2;
        cbMenuItem2 = new JCheckBoxMenuItem("blastn");
        cbMenuItem2.setMnemonic(KeyEvent.VK_C);
        cbMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "blastn";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem2.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem3;
        cbMenuItem3 = new JCheckBoxMenuItem("blastx");
        cbMenuItem3.setMnemonic(KeyEvent.VK_C);
        cbMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "blastx";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem3.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem4;
        cbMenuItem4 = new JCheckBoxMenuItem("megablast");
        cbMenuItem4.setMnemonic(KeyEvent.VK_C);
        cbMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "megablast";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem4.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem5;
        cbMenuItem5 = new JCheckBoxMenuItem("tblastn");
        cbMenuItem5.setMnemonic(KeyEvent.VK_C);
        cbMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "tblastn";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem5.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem6;
        cbMenuItem6 = new JCheckBoxMenuItem("tblastx");
        cbMenuItem6.setMnemonic(KeyEvent.VK_C);
        cbMenuItem6.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastProgram = "tblastx";
                for(int i = 0; i < program.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)program.getItem(i)).setSelected(false);
                }
                cbMenuItem6.setSelected(true);
            }
        });
        
        
        
        program.add(cbMenuItem1);
        program.add(cbMenuItem2);
        program.add(cbMenuItem3);
        program.add(cbMenuItem4);
        program.add(cbMenuItem5);
        program.add(cbMenuItem6);
        return program;
        
    }
    private JMenu makeMenuDatabase(){
         final JMenu database = new JMenu("BLAST Database");
         //set default
        final JCheckBoxMenuItem cbMenuItem1;
        cbMenuItem1 = new JCheckBoxMenuItem("nr");
        cbMenuItem1.setMnemonic(KeyEvent.VK_C);
        cbMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastDatabase = "nr";
                for(int i = 0; i < database.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)database.getItem(i)).setSelected(false);
                }
                cbMenuItem1.setSelected(true);
            }
        });
        cbMenuItem1.setSelected(true);
        
        final JCheckBoxMenuItem cbMenuItem2;
        cbMenuItem2 = new JCheckBoxMenuItem("swissprot");
        cbMenuItem2.setMnemonic(KeyEvent.VK_C);
        cbMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastDatabase = "swissprot";
                for(int i = 0; i < database.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)database.getItem(i)).setSelected(false);
                }
                cbMenuItem2.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem3;
        cbMenuItem3 = new JCheckBoxMenuItem("est");
        cbMenuItem3.setMnemonic(KeyEvent.VK_C);
        cbMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastDatabase = "est";
                for(int i = 0; i < database.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)database.getItem(i)).setSelected(false);
                }
                cbMenuItem3.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem4;
        cbMenuItem4 = new JCheckBoxMenuItem("pdb");
        cbMenuItem4.setMnemonic(KeyEvent.VK_C);
        cbMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastDatabase = "pdb";
                for(int i = 0; i < database.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)database.getItem(i)).setSelected(false);
                }
                cbMenuItem4.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem5;
        cbMenuItem5 = new JCheckBoxMenuItem("month");
        cbMenuItem5.setMnemonic(KeyEvent.VK_C);
        cbMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalBlastDatabase = "month";
                for(int i = 0; i < database.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)database.getItem(i)).setSelected(false);
                }
                cbMenuItem5.setSelected(true);
            }
        });
//        JCheckBoxMenuItem cbMenuItem6;
//        cbMenuItem6 = new JCheckBoxMenuItem("month.nt");
//        cbMenuItem6.setMnemonic(KeyEvent.VK_C);
//        cbMenuItem6.addActionListener(new ActionListener() {
//            public void actionPerformed(final ActionEvent event) {
//                globalBlastDatabase = "month.nt";
//            }
//        });
        
        
        
        database.add(cbMenuItem1);
        database.add(cbMenuItem2);
        database.add(cbMenuItem3);
        database.add(cbMenuItem4);
        database.add(cbMenuItem5);
        
        return database;
    }
    
    private JMenu makeMenuMatrix(){
        final JMenu matrix = new JMenu("Matrix");

        final JCheckBoxMenuItem cbMenuItem1;
        cbMenuItem1 = new JCheckBoxMenuItem("Blosum-30");
        cbMenuItem1.setMnemonic(KeyEvent.VK_C);
        cbMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum30";
                for(int i = 0; i < matrix.getItemCount(); i++){     
                  ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem1.setSelected(true);
            }
        });
     
        final JCheckBoxMenuItem cbMenuItem2;
        cbMenuItem2 = new JCheckBoxMenuItem("Blosum-35");
        cbMenuItem2.setMnemonic(KeyEvent.VK_C);
        cbMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum35";
                for(int i = 0; i < matrix.getItemCount(); i++){       
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem2.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem3;
        cbMenuItem3 = new JCheckBoxMenuItem("Blosum-40");
        cbMenuItem3.setMnemonic(KeyEvent.VK_C);
        cbMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum40";
                for(int i = 0; i < matrix.getItemCount(); i++){     
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem3.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem4;
        cbMenuItem4 = new JCheckBoxMenuItem("Blosum-45");
        cbMenuItem4.setMnemonic(KeyEvent.VK_C);
        cbMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum45";
                for(int i = 0; i < matrix.getItemCount(); i++){      
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem4.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem5;
        cbMenuItem5 = new JCheckBoxMenuItem("Blosum-50");
        cbMenuItem5.setMnemonic(KeyEvent.VK_C);
        cbMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum50";
                for(int i = 0; i < matrix.getItemCount(); i++){     
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem5.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem6;
        cbMenuItem6 = new JCheckBoxMenuItem("Blosum-55");
        cbMenuItem6.setMnemonic(KeyEvent.VK_C);
        cbMenuItem6.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum55";
                for(int i = 0; i < matrix.getItemCount(); i++){
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem6.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem7;
        cbMenuItem7 = new JCheckBoxMenuItem("Blosum-60");
        cbMenuItem7.setMnemonic(KeyEvent.VK_C);
        cbMenuItem7.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum60";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem7.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem8;
        cbMenuItem8 = new JCheckBoxMenuItem("Blosum-62");
        cbMenuItem8.setMnemonic(KeyEvent.VK_C);
        cbMenuItem8.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum62";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem8.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem9;
        cbMenuItem9 = new JCheckBoxMenuItem("Blosum-65");
        cbMenuItem9.setMnemonic(KeyEvent.VK_C);
        cbMenuItem9.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum65";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem9.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem10;
        cbMenuItem10 = new JCheckBoxMenuItem("Blosum-70");
        cbMenuItem10.setMnemonic(KeyEvent.VK_C);
        cbMenuItem10.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum70";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem10.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem11;
        cbMenuItem11 = new JCheckBoxMenuItem("Blosum-75");
        cbMenuItem11.setMnemonic(KeyEvent.VK_C);
        cbMenuItem11.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum75";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem11.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem12;
        cbMenuItem12 = new JCheckBoxMenuItem("Blosum-80");
        cbMenuItem12.setMnemonic(KeyEvent.VK_C);
        cbMenuItem12.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum80";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem12.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem13;
        cbMenuItem13 = new JCheckBoxMenuItem("Blosum-85");
        cbMenuItem13.setMnemonic(KeyEvent.VK_C);
        cbMenuItem13.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum85";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem13.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem14;
        cbMenuItem14 = new JCheckBoxMenuItem("Blosum-90");
        cbMenuItem14.setMnemonic(KeyEvent.VK_C);
        cbMenuItem14.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum90";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem14.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem15;
        cbMenuItem15 = new JCheckBoxMenuItem("Blosum-95");
        cbMenuItem15.setMnemonic(KeyEvent.VK_C);
        cbMenuItem15.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum95";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem15.setSelected(true);
            }
        });
        final JCheckBoxMenuItem cbMenuItem16;
        cbMenuItem16 = new JCheckBoxMenuItem("Blosum-100");
        cbMenuItem16.setMnemonic(KeyEvent.VK_C);
        cbMenuItem16.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "blosum100";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem16.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem17;
        cbMenuItem17 = new JCheckBoxMenuItem("Gonnet-250");
        cbMenuItem17.setMnemonic(KeyEvent.VK_C);
        cbMenuItem17.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "gonnet250";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem17.setSelected(true);
            }
        });
        
         final JCheckBoxMenuItem cbMenuItem18;
        cbMenuItem18 = new JCheckBoxMenuItem("Pam-250");
        cbMenuItem18.setMnemonic(KeyEvent.VK_C);
        cbMenuItem18.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "pam250";
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem18.setSelected(true);
            }
        });
        
        final JCheckBoxMenuItem cbMenuItem19;
        cbMenuItem19 = new JCheckBoxMenuItem("User-Entered Matrix");
        cbMenuItem19.setMnemonic(KeyEvent.VK_C);
        cbMenuItem19.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                globalMatrix = "UserDefined";
                createUserMatrix();
                for(int i = 0; i < matrix.getItemCount(); i++){        
                        ((JCheckBoxMenuItem)matrix.getItem(i)).setSelected(false);
                }
                cbMenuItem19.setSelected(true);

            }
        });

        
        //blosum 62 is default
        cbMenuItem8.setSelected(true);
        
        matrix.add(cbMenuItem1);
        matrix.add(cbMenuItem2);
        matrix.add(cbMenuItem3);
        matrix.add(cbMenuItem4);
        matrix.add(cbMenuItem5);
        matrix.add(cbMenuItem6);
        matrix.add(cbMenuItem7);
        matrix.add(cbMenuItem8);
        matrix.add(cbMenuItem9);
        matrix.add(cbMenuItem10);
        matrix.add(cbMenuItem11);
        matrix.add(cbMenuItem12);
        matrix.add(cbMenuItem13);
        matrix.add(cbMenuItem14);
        matrix.add(cbMenuItem15);
        matrix.add(cbMenuItem16);
        matrix.add(cbMenuItem17);
        matrix.add(cbMenuItem18);
        matrix.add(cbMenuItem19);
        
        
        
        return matrix;
    }
    private JMenu makeMenuFile(){
        JMenu file = new JMenu("File");

        file.setMnemonic(KeyEvent.VK_F);//alt F shortcut

        JMenuItem eMenuItem = new JMenuItem("New Instance");
        eMenuItem.setToolTipText("Create a new instance of NEVBLAST");
        eMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            new NevBlastGui().setVisible(true);
                        } catch (IOException ex) {
                            Logger.getLogger(NevBlastGui.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });

        JMenuItem eMenuItem1 = new JMenuItem("Restart");
        eMenuItem1.setToolTipText("Close current instance and open new");
        eMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //a.awt.EventQueue.
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            new NevBlastGui().setVisible(true);
                        } catch (IOException ex) {
                            Logger.getLogger(NevBlastGui.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                });
    
                 destroyGui();
               
            }
        });

        JMenuItem eMenuItem2 = new JMenuItem("Exit");
        eMenuItem2.setToolTipText("Exit this instance of NEVBLAST");
        eMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {

                destroyGui();
            }
        });

        JMenuItem eMenuItem3 = new JMenuItem("Exit All");
        eMenuItem3.setToolTipText("Exit all instances of NEVBLAST");
        eMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {

                System.exit(0);
            }
        });
        file.add(eMenuItem);
        file.add(eMenuItem1);
        file.add(eMenuItem2);
        file.add(eMenuItem3);
        return file;
    }

    public void destroyGui() {
//        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
//        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        this.dispose();
        this.setVisible(false);
        count--;
        if (count == 0) {

            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        queryNameLabel = new javax.swing.JLabel();
        fastaSequenceLabel = new javax.swing.JLabel();
        signature1Label = new javax.swing.JLabel();
        signature2Label = new javax.swing.JLabel();
        eValueLabel = new javax.swing.JLabel();
        lbl_entrezQuery = new javax.swing.JLabel();
        numOfResultsLabel = new javax.swing.JLabel();
        txt_entrezQuery = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_fastaSequence = new javax.swing.JTextArea();
        txt_signature1 = new javax.swing.JTextField();
        txt_signature2 = new javax.swing.JTextField();
        txt_eValue = new javax.swing.JTextField();
        txt_numberOfResults = new javax.swing.JTextField();
        txt_queryName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/group4/nevblast/colorsplash.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(-90, 0, 0, 0);
        getContentPane().add(jLabel10, gridBagConstraints);

        submitButton.setText("Submit");
        submitButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        submitButton.setMaximumSize(new java.awt.Dimension(55, 25));
        submitButton.setMinimumSize(new java.awt.Dimension(55, 25));
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.insets = new java.awt.Insets(60, -110, 0, 0);
        getContentPane().add(submitButton, gridBagConstraints);

        clearButton.setText("Clear");
        clearButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        clearButton.setMaximumSize(new java.awt.Dimension(55, 25));
        clearButton.setMinimumSize(new java.awt.Dimension(55, 25));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.insets = new java.awt.Insets(60, 120, 0, 0);
        getContentPane().add(clearButton, gridBagConstraints);

        queryNameLabel.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        queryNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        queryNameLabel.setText("Query Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 36, 0, 0);
        getContentPane().add(queryNameLabel, gridBagConstraints);

        fastaSequenceLabel.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        fastaSequenceLabel.setForeground(new java.awt.Color(255, 255, 255));
        fastaSequenceLabel.setText("FASTA Sequence:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 36, 0, 0);
        getContentPane().add(fastaSequenceLabel, gridBagConstraints);

        signature1Label.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        signature1Label.setForeground(new java.awt.Color(255, 255, 255));
        signature1Label.setText("Signature 1:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 36, 0, 0);
        getContentPane().add(signature1Label, gridBagConstraints);

        signature2Label.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        signature2Label.setForeground(new java.awt.Color(255, 255, 255));
        signature2Label.setText("Signature 2:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 36, 0, 0);
        getContentPane().add(signature2Label, gridBagConstraints);

        eValueLabel.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        eValueLabel.setForeground(new java.awt.Color(255, 255, 255));
        eValueLabel.setText("EValue:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 36, 0, 0);
        getContentPane().add(eValueLabel, gridBagConstraints);

        lbl_entrezQuery.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        lbl_entrezQuery.setForeground(new java.awt.Color(255, 255, 255));
        lbl_entrezQuery.setText("ENTREZ_QUERY");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 36, 0, 0);
        getContentPane().add(lbl_entrezQuery, gridBagConstraints);

        numOfResultsLabel.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        numOfResultsLabel.setForeground(new java.awt.Color(255, 255, 255));
        numOfResultsLabel.setText("Number Of Results:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 36, 0, 0);
        getContentPane().add(numOfResultsLabel, gridBagConstraints);

        txt_entrezQuery.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
        getContentPane().add(txt_entrezQuery, gridBagConstraints);

        jScrollPane1.setBorder(null);
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));

        txt_fastaSequence.setColumns(20);
        txt_fastaSequence.setRows(5);
        txt_fastaSequence.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane1.setViewportView(txt_fastaSequence);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 13, 0, 55);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        txt_signature1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 14, 0, 0);
        getContentPane().add(txt_signature1, gridBagConstraints);

        txt_signature2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
        getContentPane().add(txt_signature2, gridBagConstraints);

        txt_eValue.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        txt_eValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_eValueActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
        getContentPane().add(txt_eValue, gridBagConstraints);

        txt_numberOfResults.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
        getContentPane().add(txt_numberOfResults, gridBagConstraints);

        txt_queryName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 255), new java.awt.Color(0, 51, 255)));
        txt_queryName.setCaretColor(new java.awt.Color(255, 255, 255));
        txt_queryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_queryNameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 246;
        gridBagConstraints.ipady = -1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 14, 0, 0);
        getContentPane().add(txt_queryName, gridBagConstraints);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/group4/nevblast/blast mini logo.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 12;
        gridBagConstraints.ipadx = -2;
        gridBagConstraints.ipady = 56;
        gridBagConstraints.insets = new java.awt.Insets(-160, 25, 0, 0);
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/group4/nevblast/black backgroundLarge.jpg"))); // NOI18N
        jLabel9.setMaximumSize(null);
        jLabel9.setMinimumSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -233;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(jLabel9, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_queryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_queryNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_queryNameActionPerformed

    private void txt_eValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_eValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_eValueActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
       jLabel10.setVisible(true);
        getContentPane().validate();
        getContentPane().repaint();
       clearButton.setEnabled(false);
       submitButton.setEnabled(false);
        (new SubmitHelper()).execute();
        // TODO add your handling code here:
    }//GEN-LAST:event_submitButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
         txt_eValue.setText(null);
        txt_fastaSequence.setText(null);
        txt_signature1.setText(null);
        txt_signature2.setText(null);
        txt_numberOfResults.setText(null);
        txt_queryName.setText(null);
        
// TODO add your handling code here:
    }//GEN-LAST:event_clearButtonActionPerformed
    private void createUserMatrix() {
        final JFrame f = new JFrame("User-Entered Matrix");
        // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = f.getContentPane();


        MySubstitutionMatrixHelper defaultMatrix = new MySubstitutionMatrixHelper();

        Object columns[] = { "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V", "B", "Z", "X", "*"};

        final JTable userMatrixTable = new JTable(defaultMatrix.defaultMatrix, columns);

        JScrollPane scrollPane = new JScrollPane(userMatrixTable);
        RowNumberTable rowTable = new RowNumberTable(userMatrixTable);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rowTable.getTableHeader());
        
        
        
        JButton matrixSubmit = new JButton();
        matrixSubmit.setText("Submit");
        matrixSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                f.setVisible(false);
                submitUserMatrix(userMatrixTable);
            }
        });
//        JButton matrixClear = new JButton();
//        matrixClear.setText("Clear");
//        matrixClear.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                clearTableData(userMatrixTable);
//                
//            }
//        });
//        
        
        
        
        content.add(scrollPane, BorderLayout.CENTER);

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                submitUserMatrix(userMatrixTable);
            }
        });
        
        
        content.add(matrixSubmit, BorderLayout.SOUTH);
  //      content.add(matrixClear, BorderLayout.AFTER_LINE_ENDS);
        f.setSize(800, 600);
        f.setVisible(true);

    }

    private void submitUserMatrix(JTable userMatrixTable) {
        Object[][] A = getTableData(userMatrixTable);

        userMatrix = new int[A.length][A[0].length];
       // Object[][] A=getTableData(table);
        for (int i = 0; i < A.length; i++) {
       //     System.out.println();
            for (int j = 0; j < A[i].length; j++) {              
                userMatrix[i][j] = Integer.valueOf(A[i][j].toString());
          //      System.out.print(userMatrix[i][j] + " ");
            }
        }
        
   // }//end i

    }
    public static Object[][] getTableData (JTable table) {
  //  DefaultTableModel dtm = (DefaultTableModel) table.getModel();
    int nRow = table.getRowCount(), nCol = table.getColumnCount();
    Object[][] tableData = new Object[nRow][nCol];
    for (int i = 0 ; i < nRow ; i++)
        for (int j = 0 ; j < nCol ; j++){
            tableData[i][j] = table.getValueAt(i,j);
           
        }
    return tableData;
}
    public static void clearTableData (JTable table) {
  //  DefaultTableModel dtm = (DefaultTableModel) table.getModel();
    int nRow = table.getRowCount(), nCol = table.getColumnCount();
    for (int i = 0 ; i < nRow ; i++)
        for (int j = 0 ; j < nCol ; j++){
            table.setValueAt("0", i, j);
    
        }

}
   

    private void sanitizeInput() {
        //set all varaibles
        /**
         * BEGIN SIGNATURE RETRIEVAL
         *
         * Technique used: hardcode valid numbers and letters allowed iterate
         * through each sequence seperating the numbers from the letters put
         * each combination into its own bin if the bins are mismatched push an
         * error onto the stack if an invalid character is entered keep track of
         * it and continue to the next char
         *
         * Each sequence will be placed into an arrayList of combinations
         *
         */
        ArrayList<String> letterSig1 = new ArrayList<String>();
        ArrayList<String> numberSig1 = new ArrayList<String>();
        ArrayList<String> letterSig2 = new ArrayList<String>();
        ArrayList<String> numberSig2 = new ArrayList<String>();
        sig1 = new Signature();     //data format to be used
        sig2 = new Signature();     //data format to be used
        String validNumbers = "1234567890";
        String validLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ*-";
        String currentNumber = "";

        signature1 = txt_signature1.getText();
        for (int i = 0; i < signature1.length(); i++) {
            if (validNumbers.contains(Character.toString(Character.toUpperCase(signature1.charAt(i))))) {
                //valid number
                currentNumber = currentNumber + Character.toString(Character.toUpperCase(signature1.charAt(i)));
            } else if (validLetters.contains(Character.toString(Character.toUpperCase(signature1.charAt(i))))) {
                //valid letter
                letterSig1.add(Character.toString(Character.toUpperCase(signature1.charAt(i))));
                if (currentNumber.length() > 0) {
                    numberSig1.add(currentNumber);
                    currentNumber = "";
                }//end if we have a currentNumber
            } else if (signature1.charAt(i) == ' ') {
                continue;
            } else {
                signatureErrorChars = signatureErrorChars + Character.toString(Character.toUpperCase(signature1.charAt(i)));
            }

        }//end for
        if (currentNumber.length() > 0) {
            numberSig1.add(currentNumber);
            currentNumber = "";
        }//end if we have a currentNumber

        if (letterSig1.size() == numberSig1.size()) {             //if valid match rebuild signature
            for (int k = 0; k < letterSig1.size(); k++) {
                SignatureBit sigPair = new SignatureBit();
                sigPair.setLineNumber(Integer.valueOf(numberSig1.get(k)));
                sigPair.setAminoAcid(letterSig1.get(k).charAt(0));

                sig1.addSignatureBit(sigPair);
            }//end k
        }//end if valid
        else {
            errors.add("Invalid Signature 1, Letters and numbers mismatch\n");
        }//end else

        signature2 = txt_signature2.getText();

        for (int i = 0; i < signature2.length(); i++) {
            if (validNumbers.contains(Character.toString(Character.toUpperCase(signature2.charAt(i))))) {
                //valid number
                currentNumber = currentNumber + Character.toString(Character.toUpperCase(signature2.charAt(i)));
            } else if (validLetters.contains(Character.toString(Character.toUpperCase(signature2.charAt(i))))) {
                //valid letter
                letterSig2.add(Character.toString(Character.toUpperCase(signature2.charAt(i))));
                if (currentNumber.length() > 0) {
                    numberSig2.add(currentNumber);
                    currentNumber = "";
                }//end if we have a currentNumber
            } else if (signature2.charAt(i) == ' ') {
                continue;
            } else {
                signatureErrorChars = signatureErrorChars + Character.toString(Character.toUpperCase(signature2.charAt(i)));
            }

        }//end for
        if (currentNumber.length() > 0) {
            numberSig2.add(currentNumber);
            currentNumber = "";
        }//end if we have a currentNumber

        if (letterSig2.size() == numberSig2.size()) {             //if valid match rebuild signature
            for (int k = 0; k < letterSig2.size(); k++) {
                SignatureBit sigPair = new SignatureBit();
                sigPair.setLineNumber(Integer.valueOf(numberSig2.get(k)));
                sigPair.setAminoAcid(letterSig2.get(k).charAt(0));

                System.out.println(Integer.valueOf(validLetters.indexOf(letterSig2.get(k))));
                sig2.addSignatureBit(sigPair);
            }//end k
        }//end if valid
        else {
            errors.add("Invalid Signature 2, Letters and numbers mismatch\n");
        }//end else

        /**
         * END SIGNATURE RETRIEVAL
         */
        eValue = txt_eValue.getText();

        if (isScientificNotation(eValue)) {
            eValueDecimal = new BigDecimal(eValue);
        } else {
            errors.add("Invalid EValue, please use scientific format(7.001e-2) or long decimal(0.07001)\n");
        }

        fastaSequence = txt_fastaSequence.getText();
        numberOfResults = txt_numberOfResults.getText();
        queryName = txt_queryName.getText();

        //TODO scrub them of malicious input
        System.out.println(sig1.toString());
        System.out.println(sig2.toString());
        
        
        //get the entrez query
       // entrezQuery = txt_entrezQuery.getText();
        entrezQuery = txt_entrezQuery.getSelectedItem().toString();
        System.out.println(entrezQuery);
    }//end sanitize input

    boolean isScientificNotation(String numberString) {
        // Validate number
        try {
            new BigDecimal(numberString);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public void createOutput(ArrayList<SequenceHit> toGraph) throws IOException, SecurityException, UnsatisfiedLinkError, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        //if graph is contains only one element and its error code is not empty
        if (toGraph.size() == 1 && !toGraph.get(0).getError().isEmpty()) {
            String error = "Blast reported that no results back, please see error message\n\n\n";
            JOptionPane.showMessageDialog(new JFrame(), toGraph.get(0).getError());
            jLabel10.setVisible(false);
            return;
        }
//        setLibraryPath("");
        desktopOutput = new JDesktopPane();
        setExtendedState(MAXIMIZED_BOTH);
        // desktopOutput.
        String outputWindowHeader;

        outputWindowHeader = "Fasta Header: " + fastaHead + "<br>";
        outputWindowHeader += "Fasta Sequence: " + fastaSequence + "<br>";
        outputWindowHeader += "Signature A: " + signature1 + "<br>";
        outputWindowHeader += "Signature B: " + signature2 + "<br><br><br>";
        JTextPane outputWindow = new JTextPane();

        outputWindow.setSize(500, 200);
        outputWindow.setContentType("text/html");
        outputWindow.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        JScrollPane scrollPane = new JScrollPane(outputWindow);

        Grapher chart3d = new Grapher(toGraph, outputWindow, outputWindowHeader, "3dChart");
        chart3d.init();
        Grapher chart2dA = new Grapher(toGraph, outputWindow, outputWindowHeader, "2dChartA", chart3d.getDefinedColors());
        chart2dA.init();
        Grapher chart2dB = new Grapher(toGraph, outputWindow, outputWindowHeader, "2dChartB", chart3d.getDefinedColors());
        chart2dB.init();

        ResultsTable newContentPane = new ResultsTable(toGraph, toGraph.size(), outputWindow, outputWindowHeader, chart3d.scatter, chart2dA.scatter2dA, chart2dB.scatter2dB);       //only works for 3d
        chart3d.attachResultTable(newContentPane);
        chart2dA.attachResultTable(newContentPane);
        chart2dB.attachResultTable(newContentPane);

        chart3d.attachScatters(chart2dA.scatter2dA, chart2dB.scatter2dB);
        chart2dA.attachScatters(chart3d.scatter, chart2dB.scatter2dB);
        chart2dB.attachScatters(chart2dA.scatter2dA, chart3d.scatter);

        chart3d.attachMouse();
        chart2dA.attachMouse();
        chart2dB.attachMouse();

        createFrame("Result 3d", chart3d, 345, 0, 1000, 690);
        createFrame("Result 2dA", chart2dA, 0, 0, 345, 345);
        createFrame("Result 2dB", chart2dB, 0, 345, 345, 345);
        createFrame("Text Output", scrollPane);
        createFrame("Results Table", newContentPane);

        setContentPane(desktopOutput);
        desktopOutput.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

    }

    protected void createFrame(String windowName, Grapher chart, int xpos, int ypos, int sizeW, int sizeH) {
        MyInternalFrame frame = new MyInternalFrame(windowName);
        frame.setVisible(true);

        // Set up the default size of the chart and add it to the frame.
        Component jComp = (java.awt.Component) chart.getChart().getCanvas();
        Dimension dim = new Dimension(sizeW, sizeH);
        jComp.setMaximumSize(dim);
        jComp.setPreferredSize(dim);
        jComp.setSize(dim);
        jComp.setMinimumSize(dim);
        frame.add(jComp);

        frame.setSize(sizeW, sizeH);
        frame.setLocation(xpos, ypos);

        desktopOutput.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    //create the resultsTable on the bottom portion of the screen
    protected void createFrame(String windowName, ResultsTable resultsTable) {
        MyInternalFrame frame = new MyInternalFrame(windowName);
        frame.setVisible(true);
        resultsTable.setOpaque(true);

        frame.add(resultsTable);

        frame.setSize(getWidth(), getHeight() / 3);
        frame.setLocation(0, getHeight() - (getHeight() / 3) - 30);
        desktopOutput.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame(String windowName, JScrollPane resultsText) {
        MyInternalFrame frame = new MyInternalFrame(windowName);
        frame.setVisible(true);
        // resultsText.setOpaque(true);
        frame.add(resultsText);

        frame.setSize(510, 690);
        frame.setLocation(1345, 0);

        desktopOutput.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }
    class SubmitHelper extends SwingWorker<String, Object> {

    @Override
    public String doInBackground() {
        errors = new ArrayList<String>();
        InputStream stream = null;

        sanitizeInput();
        System.out.println(signature1);
        System.out.println(signature2);
        if (errors.size() > 0) {
            JOptionPane.showMessageDialog(new JFrame(), errors.toString().substring(1, errors.toString().length() - 1));
            jLabel10.setVisible(false);
            return "";
        }//if erros stop
        try {
            //we must parse fastaSequence
            String fasta = fastaSequence;
            stream = new ByteArrayInputStream(fasta.getBytes("UTF-8"));

            try {

                LinkedHashMap<String, ProteinSequence> map = FastaReaderHelper.readFastaProteinSequence(stream);
                        
                //iterate through FASTA sequences
                for (Entry<String, ProteinSequence> entry : map.entrySet()) {
                    String fastaHeader = entry.getValue().getOriginalHeader();
                    System.out.println(entry.getValue().getDescription());
                    fastaSequence = entry.getValue().getSequenceAsString();

                    fasta = fasta.replaceAll("(\\r|\\n)", "");
                    System.out.println(fastaHeader);
                 //   fastaHeader = fasta.substring(0, fasta.length() - fastaSequence.length());
                    System.out.println(fastaHeader);
                    System.out.println("Fasta Header: " + fastaHeader + "\nFasta sequence: " + fastaSequence);

                    fastaHead = fastaHeader;
                    //create BlastQuery Object
                    BlastQuery blastQuery = new BlastQuery(queryName, sig1,
                            sig2, fastaSequence, fastaHeader, eValueDecimal, numberOfResults, "", globalMatrix, userMatrix,globalBlastProgram, globalBlastDatabase, entrezQuery);

                    createOutput(blastQuery.toBlast());

                }//end foreach fasta
            } catch (Exception ex) {
                Logger.getLogger(NevBlastGui.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("exception ex");
                JOptionPane.showMessageDialog(new JFrame(), "An unexpected exception arose, please restart program.");
                jLabel10.setVisible(false);

            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NevBlastGui.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(new JFrame(), "An unsupported encoding exception arose, please restart program.");
            jLabel10.setVisible(false);

        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(NevBlastGui.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(new JFrame(), "An unexpected exception arose, please restart program.");
                jLabel10.setVisible(false);

            }
        }//end finally
        return "";
    }

//        @Override
//        protected void done() {
//            try {
//                jLabel10.setVisible(true);
//            } catch (Exception ignore) {
//            }
//        }
}
/**
 * @param args the command line arguments
 */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(NevBlastGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(NevBlastGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(NevBlastGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(NevBlastGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new NevBlastGui().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel eValueLabel;
    private javax.swing.JLabel fastaSequenceLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_entrezQuery;
    private javax.swing.JLabel numOfResultsLabel;
    private javax.swing.JLabel queryNameLabel;
    private javax.swing.JLabel signature1Label;
    private javax.swing.JLabel signature2Label;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField txt_eValue;
    private javax.swing.JComboBox txt_entrezQuery;
    private javax.swing.JTextArea txt_fastaSequence;
    private javax.swing.JTextField txt_numberOfResults;
    private javax.swing.JTextField txt_queryName;
    private javax.swing.JTextField txt_signature1;
    private javax.swing.JTextField txt_signature2;
    // End of variables declaration//GEN-END:variables
    private String eValue;                  //this holds the raw string
    private BigDecimal eValueDecimal;       //the value to be passed
    private String fastaSequence;
    private String fastaHead;
    private String numberOfResults;
    private String queryName;
    private String signature1;              //This holds the raw string
    private String signature2;              //This holds the raw string
    private ArrayList<String> errors;
    private Signature sig1;     //data format to be used
    private Signature sig2;     //data format to be used
    private String signatureErrorChars;
    private JDesktopPane desktopOutput;
    private String globalMatrix;
    private String globalBlastDatabase;
    private String globalBlastProgram;
    private int[][] userMatrix;
    private String entrezQuery;
}
