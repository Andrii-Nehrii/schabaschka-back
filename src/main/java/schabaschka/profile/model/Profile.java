package schabaschka.profile.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_id" ,  nullable = false)
    private Long userId;





    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @Column(name = "phone")
    private String phone;



    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
        this.userId = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Profile(Long userId, String name, String city, String phone) {
        this.userId = userId;
        this.name = name;
        this.city = city;
        this.phone = phone;
    }

    public Profile() {
    }
}



















