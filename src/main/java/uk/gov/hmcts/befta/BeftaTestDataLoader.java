package uk.gov.hmcts.befta;

public interface BeftaTestDataLoader {

    public static void main(String[] args) {
        BeftaMain.getAdapter().getDataLoader().loadTestDataIfNecessary();
    }

    void loadTestDataIfNecessary();

    boolean isTestDataLoadedForCurrentRound();

}
