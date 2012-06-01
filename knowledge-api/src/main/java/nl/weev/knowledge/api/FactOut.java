package nl.weev.knowledge.api;

import java.io.Serializable;

public class FactOut implements Serializable {

    private static final long serialVersionUID = 7877486775662721272L;

    private Root root;

    public FactOut(Root root) {
        super();
        this.root = root;
    }

    public Root getRoot() {
        return root;
    }

}
