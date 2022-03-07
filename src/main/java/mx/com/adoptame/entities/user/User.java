package mx.com.adoptame.entities.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mx.com.adoptame.entities.donation.Donation;
import mx.com.adoptame.entities.news.News;
import mx.com.adoptame.entities.pet.entities.Pet;
import mx.com.adoptame.entities.pet.entities.PetAdopted;
import mx.com.adoptame.entities.profile.Profile;
import mx.com.adoptame.entities.role.Role;
import mx.com.adoptame.entities.request.Request;

@Entity
@Table(name = "TBL_USERS")
@Data
@NoArgsConstructor
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String password;

    @Column(name = "is_active", nullable = false, columnDefinition = "tinyint default 1")
    private Boolean isActive;

    @Column(name = "link_restore_password", columnDefinition = "varchar(150)")
    private String linkRestorePassword;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    // Relationships

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToOne(mappedBy = "user")
    private Request request;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<PetAdopted> adoptedPets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Pet> pets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<News> news;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Donation> donations;

    public void addDonation(Donation donation) {
        donations.add(donation);
        donation.setUser(this);
    }

    // Many to Many: PETS
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "TBL_FAVORITES_PETS",
            joinColumns = @JoinColumn(name = "id_pet"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    private List<Pet> favoitesPets = new ArrayList<>();

    public void addToFavorite(Pet pet) {
        favoitesPets.add(pet);
        pet.getUsers().add(this);
    }

    public void removeFromFavorite(Pet pet) {
        favoitesPets.remove(pet);
        pet.getUsers().remove(this);
    }
}
