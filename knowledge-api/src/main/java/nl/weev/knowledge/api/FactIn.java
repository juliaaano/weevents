package nl.weev.knowledge.api;

import java.io.Serializable;

public class FactIn implements Serializable {

    private static final long serialVersionUID = -2911492309909603059L;

    private Root root;

    public FactIn(Root root) {
        super();
        this.root = root;
    }

    public Root getRoot() {
        return root;
    }

}
