class DevProfile {
    private Object id;
    private String email;
    private String password;

    DevProfile(String email, String password) {
        this.id = null;
        this.email = email;
        this.password = password;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public DevProfile setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DevProfile setPassword(String password) {
        this.password = password;
        return this;
    }
}

class DevProfileMother {
    static DevProfile alice() {
        return new DevProfile("alice@gmail.com", "qwerty");
    }

    static DevProfile bob() {
        return new DevProfile("bob@example.net", "12345");
    }

    static DevProfile withBadEmail(String email) {
        var password = String.valueOf(System.currentTimeMillis());
        return new DevProfile(email, password);
    }

    static DevProfile withBadPassword(String password) {
        var email = "user-" + System.currentTimeMillis();
        return new DevProfile(email, password);
    }
}
