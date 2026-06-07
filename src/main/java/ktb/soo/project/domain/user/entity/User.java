package ktb.soo.project.domain.user.entity;

import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = null;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updatePassword(String password){
        this.password = password;
    }
}
