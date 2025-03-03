package fr.teampeps.model.member;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@DiscriminatorValue("OPPONENT")
public class OpponentMember extends Member {

}
