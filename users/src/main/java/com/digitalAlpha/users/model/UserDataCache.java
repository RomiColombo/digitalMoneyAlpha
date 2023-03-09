package com.digitalAlpha.users.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDataCache {

   private String userId;
   private String accountId;
   private String cvu;
   private String alias;

}
