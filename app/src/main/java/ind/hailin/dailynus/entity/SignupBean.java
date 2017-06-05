package ind.hailin.dailynus.entity;

/**
 * Created by hailin on 2017/5/31.
 */

public class SignupBean {

    private String username;
    private String password;
    private String nickname;

    public SignupBean(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "username=" + username + ",password=" + password + ",nickname=" + nickname;
    }
}
