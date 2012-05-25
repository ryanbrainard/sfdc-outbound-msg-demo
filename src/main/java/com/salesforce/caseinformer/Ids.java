package com.salesforce.caseinformer;

public class Ids {

    private Ids() {}

    private static final int LENGTH = 15;

    public static String validate(String id) {
        if (null == id)  {
            throw new IllegalArgumentException("Id must not be null");
        }

        id = id.trim();

        if (id.length() > LENGTH) {
            id = id.substring(0, LENGTH);
        }

        if (!id.matches("\\w{" + LENGTH + "}")) {
            throw new IllegalArgumentException("Invalid id format: " + id);
        }

        return id;
    }
}
