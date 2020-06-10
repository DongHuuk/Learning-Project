package com.providelearingsite.siteproject.learning;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter @Setter
@Embeddable
public class EmbeddedLearningAndAccount implements Serializable {

    @Column(name = "learning_id")
    private Long learning;
    @Column(name = "account_id")
    private Long account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddedLearningAndAccount that = (EmbeddedLearningAndAccount) o;
        return Objects.equals(learning, that.learning) &&
                Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(learning, account);
    }
}
