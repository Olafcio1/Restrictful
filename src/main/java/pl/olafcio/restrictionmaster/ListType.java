package pl.olafcio.restrictionmaster;

import org.jetbrains.annotations.Contract;

public enum ListType {
    WHITE("whitelist", false),
    BLACK("blacklist", true);

    public final String id;
    public final boolean cancel;

    ListType(String id, boolean cancel) {
        this.id = id;
        this.cancel = cancel;
    }

    public static class ValueNotFoundError extends RuntimeException {
        private ValueNotFoundError() {
            super("ListType[WHITE, BLACK] cannot match null");
        }

        private ValueNotFoundError(String value) {
            super("ListType[WHITE, BLACK] cannot match '" + value + "'");
        }
    }

    @Contract("null -> fail")
    public static ListType of(String id) throws ValueNotFoundError {
        if (id == null)
            throw new ValueNotFoundError();
        else if (id.equals("WHITELIST"))
            return ListType.WHITE;
        else if (id.equals("BLACKLIST"))
            return ListType.BLACK;
        else throw new ValueNotFoundError(id);
    }
}
