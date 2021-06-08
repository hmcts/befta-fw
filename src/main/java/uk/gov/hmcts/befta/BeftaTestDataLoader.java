package uk.gov.hmcts.befta;

public interface BeftaTestDataLoader {

    public static void main(String[] args) {
        BeftaMain.getAdapter().getDataLoader().loadDataIfNotLoadedVeryRecently();
    }

    void loadDataIfNotLoadedVeryRecently();

    boolean isTestDataLoadedForCurrentRound();

}
