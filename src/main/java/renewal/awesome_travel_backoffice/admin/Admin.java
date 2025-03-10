package renewal.awesome_travel_backoffice.admin;

import jakarta.persistence.*;
import lombok.*;

// @Entity
// @Table
@Getter
@Setter
public class Admin {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long admin_id;

  @Column(unique = true)
  private String id;

  @Column
  private String password;

}
