package com.providelearingsite.siteproject.learning;

import com.providelearingsite.siteproject.account.Account;
import com.providelearingsite.siteproject.question.Question;
import com.providelearingsite.siteproject.review.Review;
import com.providelearingsite.siteproject.tag.Tag;
import com.providelearingsite.siteproject.video.Video;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Learning {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String lecturerName;
    @NotNull
    private String lecturerDescription;
    @NotNull

    private String simplesubscription;
    @Lob
    private String subscription;
    @Lob
    private String bannerBytes;
    private String bannerServerPath;
    private String kategorie;

    private int price;

    @Nullable
    private LocalDateTime createLearning;
    @Nullable
    private LocalDateTime openLearning = null;
    @Nullable
    private LocalDateTime closeLearning = null;
    @Nullable
    private LocalDateTime uploadVideo = null;
    @Nullable
    private LocalDateTime updateLearning = null;

    private LocalDateTime buyLearning;

    private boolean startingLearning = false;
    private boolean closedLearning = true;

    private int videoCount = 0;

    private String comment; // 후기
    private float rating = 0;
    private Integer totalPrice;

    //듣고 있는 account들
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();

    //업로더 (게시자)
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "learning", fetch = FetchType.LAZY)
    private Set<Video> videos = new HashSet<>();

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "learning")
    private Set<Review> reviews = new HashSet<>();

    public void setVideos(Video video) {
        this.videos.add(video);
        if(video.getLearning() != this){
            video.setLearning(this);
        }
    }

    public void setReviews(Review review) {
        this.reviews.add(review);
        if(review.getLearning() != this){
            review.setLearning(this);
        }
    }

    public void setAccount(Account account) {
        this.account = account;
        if(!account.getLearnings().contains(this)){
            account.getLearnings().add(this);
        }
    }
    public int getRating_int(){
        return (int)Math.floor(this.rating);
    }
    public boolean checkRating_boolean(){
        int rating_double = getRating_int();
        return ((this.rating * 10) - (rating_double * 10)) >= 5 && rating_double <= 5;
    }
    public int emptyRating(){
        int rating_double = getRating_int();
        boolean halfRating = checkRating_boolean();
        return 5 - rating_double - (halfRating ? 1 : 0);
    }
    public String price_comma(){
        StringBuilder str = new StringBuilder().append(this.price);
        int index = str.length();
        int insertTime = index - 3;
        float i = index / 3;

        if(index <= 1){
            return "free";
        }

        for (int j=0; j < i; j++){
            if(index % 3 <= 0){
                i--;
            }
            if(index <= 3){
                return String.valueOf(str);
            }
            str.insert(insertTime, ',');
            insertTime -= 3;
        }
        return String.valueOf(str);
    }
    public boolean checkUploadLearning(){
        return !(this.uploadVideo instanceof LocalDateTime) || this.uploadVideo == null;
    }
    public boolean checkUpdateLearning(){
        return !(this.updateLearning instanceof LocalDateTime) || this.updateLearning == null;
    }
    public String getKategorieValue(){
        switch (this.kategorie) {
            case "1":
                return "Web";
            case "2":
                return "algorithm";
            default:
                return "none";
        }
    }
}
