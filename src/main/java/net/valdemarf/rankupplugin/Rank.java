package net.valdemarf.rankupplugin;

public class Rank {

    private final String name;
    private final double startPrice;
    private final int identifier;

    public Rank(int identifier, String name, double startPrice) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.startPrice = startPrice;
    }

    public String getName() {
        return this.name;
    }

    public double getStartPrice() {
        return this.startPrice;
    }

    public double getPrice(PrisonPlayer player) {
        if(identifier == 0) {
            return startPrice * player.getPrestige();
        }
        return startPrice * player.getPrestige() * RankupPlugin.rankIncrement;
    }

    public int getIdentifier() {
        return identifier;
    }
}
