package com.cardbff.model;


import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

@Introspected
@Builder
@Serdeable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
    private Long id;
    private String email;
    private String name;
    private String dob;
    private String pan;
    private String mobileNo;
    @ColumnName("verified")
    private boolean isVerified;

    /*
    *  Intentionally used @ColumnName annotation on Getter and Setter explicitly for mobileNo because
    *  bindBean() only looks at annotations on the getters and setters and not the fields themselves. So when fetching
    *  data and mapping from JDBI, mobileNo wasn't getting populated.
    *  ref: https://github.com/jdbi/jdbi/issues/1296#issuecomment-435397607
    * */

    @ColumnName("mobile")
    public String getMobileNo() {
        return mobileNo;
    }

    @ColumnName("mobile")
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
