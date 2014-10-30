package uk.ac.derby.Tanq;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;

import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Core.Battleground;
import uk.ac.derby.Tanq.Core.Menu;
import uk.ac.derby.Tanq.Core.Tanq;
import uk.ac.derby.Tanq.Core.TanqRanked;
import uk.ac.derby.Tanq.Core.Version;
import uk.ac.derby.Utilities.Table.SimpleColumns.*;


/** Main class and GUI */
public class GUI extends JFrame {
	
	private static final long serialVersionUID = 0;
    private static GUI gui;
    
    private static Battleground battleground;

	public GUI() {
        initComponents();
        setJMenuBar(new Menu());
	}
	
	/**
	 * @param args - the usual
	 */
	public static void main(String[] args) {
        gui = new GUI();
        gui.setSize(1200, 748);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        gui.setLocation( screenSize.width/2 - gui.getSize().width/2, screenSize.height/2 - gui.getSize().height/2);
        gui.setVisible(true);
	}

	/** Launch a battlefield with a given background image. */
	public static void launchBattlefield(String imageName) {
		battleground.setBackground(imageName);
		battleground.start();
	}
	
	/** Add a player.  Invoke this after launchBattlefield() */
	public static void addPlayer(Brain brain, String name) {
		battleground.addPlayer(brain, name);
	}
	
	private Timer competitionTicker = null;
	private int countdown;

	private void saveResults(String fname) {
		java.util.Vector<TanqRanked> ranked = battleground.getRanking();
		File fout = new File(fname);
		PrintStream ps = null;
		try {
			ps = new PrintStream(fout);
			for (int i=0; i<ranked.size(); i++)
				ps.println(ranked.get(i).toString());
		} catch (Throwable t) {
			uk.ac.derby.Logger.Log.println(t.toString());
		} finally {
			ps.flush();
			ps.close();			
		}
		JOptionPane.showMessageDialog(null, "Winner: " + ranked.get(0), "Results", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/** Set up a time-limited competition. */
	private void setupCompetition(int minutes, final String rFname) {
		countdown = minutes * 60;
		jTimeRemaining.setMaximum(countdown);
		jTimeRemaining.setValue(countdown);
		competitionTicker = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				countdown--;
				jTimeRemaining.setValue(countdown);
				if (countdown==0) {
					saveResults(rFname);
					exit();
				}
			}
		});
		competitionTicker.setRepeats(true);
		competitionTicker.start();
	}
	
	/** Establish a time-limited competition. Time is in minutes. */
	public static void setCompetitionTime(int minutes, String outputFname) {
		gui.setupCompetition(minutes, outputFname);
	}
	
	private Vector<TanqRanked> rawRanks = null;
	
    class TableColumns extends SimpleColumns {
    	public int getRowCount() {
    		if (rawRanks == null)
    			return 0;
    		else
    			return rawRanks.size();
    	}
    	public Object getValueAt(int i) {
    		if (rawRanks == null)
    			return null;
    		else
    			return rawRanks.get(i);
    	}
    };
	
	private TableColumns rankings;
	
	/** Set up leaderboard */
	private void setupLeaderboard(JTable leaderBoard) {

	    abstract class DefaultColumn extends SimpleColumn {
	    	public boolean isEditable(Object row, int rowNumber) {
	    		return true;
	    	}
	    	public void setCellData(Object row, int rowNumber, Object dataForCell) {
	    	}
	    };
	    
	    rankings = new TableColumns();
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Icon";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getIcon();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Name";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getName();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Score";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getScore();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Injuries";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getInjuries();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Collisions";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getCollisions();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Bumps";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getBumps();
	    	}
	    });
	    rankings.add(new DefaultColumn() {
	    	public String getName() {
	    		return "Bullets";
	    	}
	    	public Object getCellData(Object row, int rowNumber) {
	    		return ((TanqRanked)row).getBulletsAvailable();
	    	}
	    });
	    
	    rankings.setReadOnly(true);
		rankings.configureTable(leaderBoard);	
	}

	private int tableRefreshBlocked = 0;
	private int refreshing = 0;
    private JProgressBar jTimeRemaining;
	
	private void initComponents() {		
	    JMenuBar jMenuBar1 = new JMenuBar();
	    JMenu FileMenu = new JMenu();
	    JMenu OptionsMenu = new JMenu();
	    JPanel jPanel1 = new JPanel();
	
	    FileMenu.setText("Menu");
	    jMenuBar1.add(FileMenu);
	
	    OptionsMenu.setText("Menu");
	    jMenuBar1.add(OptionsMenu);
	
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    setTitle(Version.getVersion());
	    addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	            exit();
	        }
	    });
	
	    jPanel1.setLayout(new BorderLayout());
	
	    getContentPane().add(jPanel1, BorderLayout.NORTH);
		
	    jTimeRemaining = new JProgressBar();
	    getContentPane().add(jTimeRemaining, BorderLayout.SOUTH);

	    battleground = new Battleground();
	    
	    JPanel arenaContainer = new JPanel();
	    arenaContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
	    arenaContainer.add(battleground);
	    
	    getContentPane().add(arenaContainer, BorderLayout.CENTER);

	    final JTable leaderBoard = new JTable();
	    leaderBoard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    leaderBoard.setColumnSelectionAllowed(false);
	    leaderBoard.setRowSelectionAllowed(true);
	    	    	    
	    class SelectionListener implements ListSelectionListener {
	        JTable table;
	    
	        // It is necessary to keep the table since it is not possible
	        // to determine the table from the event's source
	        SelectionListener(JTable table) {
	            this.table = table;
	        }
	        public void valueChanged(ListSelectionEvent e) {
	        	if (refreshing > 0)
	        		return;
	            // If cell selection is enabled, both row and column change events are fired
	            if (e.getSource() == table.getSelectionModel()
	                  && table.getRowSelectionAllowed() && !e.getValueIsAdjusting()) {
	            	tableRefreshBlocked++;
	                // Row selection changed
		    		final Tanq selected = ((TanqRanked)rankings.getValueAt(e.getFirstIndex())).getTanq();
		    		selected.setSelected(true);
		    		Timer t = new Timer(3000, new ActionListener() {
		    			public void actionPerformed(ActionEvent evt) {
		    				selected.setSelected(false);
		    				tableRefreshBlocked--;
		    			}
		    		});
		    		t.setRepeats(false);
		    		t.start();	                
	            }	    
	        }
	    }

	    SelectionListener listener = new SelectionListener(leaderBoard);
	    leaderBoard.getSelectionModel().addListSelectionListener(listener);
	    
		setupLeaderboard(leaderBoard);
	    leaderBoard.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	    
	    JPanel scoreboard = new JPanel();
	    scoreboard.setLayout(new BorderLayout());
	    
	    scoreboard.add(new JScrollPane(leaderBoard), BorderLayout.CENTER);
	    
	    getContentPane().add(scoreboard, BorderLayout.WEST);
	    
	    pack();
	    
	    arenaContainer.setBounds(0, 0, battleground.getWidth(), battleground.getHeight());
	    
	    Timer rankTime = new Timer(1000, new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		if (tableRefreshBlocked > 0)
	    			return;
	    		rawRanks = battleground.getRanking();
	    		refreshing++;
	    		rankings.refresh();
	    		refreshing--;
	    	}
	    });
	    rankTime.start();
	}
	
	public static void exit() {
		System.exit(0);
	}
}
