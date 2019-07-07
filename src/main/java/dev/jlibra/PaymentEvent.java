package dev.jlibra;

public class PaymentEvent {

    private byte[] address;
    private long amount;
    private EventPath eventPath;

    public PaymentEvent(byte[] address, long amount, EventPath eventPath) {
        this.address = address;
        this.amount = amount;
        this.eventPath = eventPath;
    }

    public byte[] getAddress() {
        return address;
    }

    public long getAmount() {
        return amount;
    }

    public EventPath getEventPath() {
        return eventPath;
    }

    public static class EventPath {

        public static final byte TAG_CODE = 0;
        public static final byte TAG_RESOURCE = 1;

        public static final String SUFFIX_SENT = "/sent_events_count/";
        public static final String SUFFIX_RECEIVED = "/sent_events_count/";

        private byte tag;
        private byte[] accountResourcePath;
        private String suffix;

        public EventPath(byte tag, byte[] accountResourcePath, String suffix) {
            this.tag = tag;
            this.accountResourcePath = accountResourcePath;
            this.suffix = suffix;
        }

        public byte getTag() {
            return tag;
        }

        public byte[] getAccountResourcePath() {
            return accountResourcePath;
        }

        public void setAccountResourcePath(byte[] accountResourcePath) {
            this.accountResourcePath = accountResourcePath;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }

}
