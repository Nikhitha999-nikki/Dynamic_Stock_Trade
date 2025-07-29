import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.io.*;
public class DynamicStock extends JFrame {
    private DefaultTableModel tableModel;
    private java.util.List<StockData> stocks = new ArrayList<StockData>();

    private JTable table;
    private JLabel portfolioLabel;
    private JButton buyButton, sellButton, saveButton;

    public DynamicStock() {
        super("Live Stock Market Viewer");
        setSize(720, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadPortfolioFromFile();
        if (stocks.isEmpty()) {
            stocks.add(new StockData("AAPL","Apple Inc.",178.35));
            stocks.add(new StockData("GOOG","Alphabet Inc.",2840.12));
            stocks.add(new StockData("AMZN","Amazon.com Inc.",3456.78));
            stocks.add(new StockData("TSLA","Tesla Inc.",223.45));
        }

        String[] cols = {"Symbol","Company","Price","Shares"};
        tableModel = new DefaultTableModel(cols,0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        portfolioLabel = new JLabel("Portfolio Value: $0.00");

        buyButton = new JButton("Buy");
        sellButton = new JButton("Sell");
        saveButton = new JButton("Save Portfolio");

        JPanel bottom = new JPanel();
        bottom.add(buyButton);
        bottom.add(sellButton);
        bottom.add(saveButton);
        bottom.add(portfolioLabel);
        add(bottom, BorderLayout.SOUTH);

        buyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    stocks.get(row).sharesOwned++;
                    updateTable();
                }
            }
        });

        sellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1 && stocks.get(row).sharesOwned > 0) {
                    stocks.get(row).sharesOwned--;
                    updateTable();
                }
            }
        });

        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                savePortfolioToFile();
            }
        });

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (int i = 0; i < stocks.size(); i++) {
                    stocks.get(i).updatePrice();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() { updateTable(); }
                });
            }
        }, 0, 5000);

        updateTable();
        setVisible(true);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        double total = 0.0;
        for (int i = 0; i < stocks.size(); i++) {
            StockData s = stocks.get(i);
            tableModel.addRow(new Object[]{s.symbol,s.company,s.price,s.sharesOwned});
            total += s.getValue();
        }
        portfolioLabel.setText("Portfolio Value: $" + String.format("%.2f", total));
    }

    private void savePortfolioToFile() {
        try {
            PrintWriter w = new PrintWriter("portfolio.txt");
            for (int i = 0; i < stocks.size(); i++) {
                StockData s = stocks.get(i);
                w.println(s.symbol + "," + s.company + "," + s.price + "," + s.sharesOwned);
            }
            w.close();
            JOptionPane.showMessageDialog(this, "Portfolio saved.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
        }
    }

    private void loadPortfolioFromFile() {
        File f = new File("portfolio.txt");
        if (!f.exists()) return;

        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");
                if (p.length == 4) {
                    StockData s = new StockData(p[0], p[1], Double.parseDouble(p[2]));
                    s.sharesOwned = Integer.parseInt(p[3]);
                    stocks.add(s);
                }
            }
            sc.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { new DynamicStock(); }
        });
    }
}
//select rows for buy/sell shares