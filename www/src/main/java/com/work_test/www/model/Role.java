package com.work_test.www.model;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  Long id;
    @Enumerated(EnumType.STRING)
   private RoleName role;

}
