class StockData {
    String symbol;
    String company;
    double price;
    int sharesOwned = 0;

    StockData(String symbol, String company, double price) {
        this.symbol  = symbol;
        this.company = company;
        this.price   = price;
    }

    void updatePrice() {
        double change = (Math.random() - 0.5) * 10;            
        price = Math.round((price + change) * 100.0) / 100.0;  
    }

    double getValue() {
        return price * sharesOwned;
    }
}