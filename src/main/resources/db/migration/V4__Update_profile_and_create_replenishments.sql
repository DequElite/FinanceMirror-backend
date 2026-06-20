CREATE TABLE replenishment (
   id UUID PRIMARY KEY,

   profile_id UUID NOT NULL,

   date DATE NOT NULL,

   amount INTEGER NOT NULL,

   description VARCHAR(255) NOT NULL,

   category VARCHAR(50),

   CONSTRAINT fk_replenishment_profile
       FOREIGN KEY (profile_id)
           REFERENCES profile(id)
           ON DELETE CASCADE
);