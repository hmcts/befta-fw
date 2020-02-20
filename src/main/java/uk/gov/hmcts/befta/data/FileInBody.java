package uk.gov.hmcts.befta.data;

import lombok.Data;

@Data
public class FileInBody {

    private String fullPath;

    private String size;

    private String contentHash;

    public FileInBody(String fullPath) {
        super();
        this.fullPath = fullPath;
    }

}
