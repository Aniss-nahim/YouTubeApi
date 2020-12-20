/*
#------------------------------------------------------------
#        Script ORACLE.
#------------------------------------------------------------
*/

CREATE TABLE Recherche(
        Id_recherche    Int  NOT NULL ,
        date_recherchce Date NOT NULL ,
        date_debut      Date NOT NULL ,
        date_fin        Date NOT NULL ,
        query           Varchar (50) NOT NULL
	,CONSTRAINT Recherche_PK PRIMARY KEY (Id_recherche)
);

/*
#------------------------------------------------------------
# Table: Channel
#------------------------------------------------------------
*/
CREATE TABLE Channel(
        id_channel      Varchar (50) NOT NULL ,
        Titre_channel   Varchar (50)  Not NULL,
        Subscribercount number(19)  NOT NULL,
        Viewcount       number(19)  NOT NULL,
        videocount      number(19) NOT NULL
	,CONSTRAINT Channel_PK PRIMARY KEY (id_channel)
);

/*
#------------------------------------------------------------
# Table: Video
#------------------------------------------------------------
*/
CREATE TABLE Video(
        ID_video     Varchar (50) NOT NULL ,
        Titre        Varchar (100) NOT NULL ,
        publishedAt  Date NOT NULL ,
        viewcount    number(19) NOT NULL ,
        likecount    number(19) NOT NULL ,
        dislikecount number(19) NOT NULL ,
        commentcount number(19) NOT NULL ,
        id_channel   Varchar (50) NOT NULL
	,CONSTRAINT Video_PK PRIMARY KEY (ID_video)
);

/*
#------------------------------------------------------------
# Table: Resultat
#------------------------------------------------------------
*/
CREATE TABLE Resultat(
        ID_video     Varchar (50) NOT NULL ,
        Id_recherche Int NOT NULL
	,CONSTRAINT Resultat_PK PRIMARY KEY (ID_video,Id_recherche)
);

/*
#------------------------------------------------------------
# Table: Commentaire
#------------------------------------------------------------
*/
CREATE TABLE Commentaire(
        comment_id             Varchar (60) NOT NULL ,
        auteur_name            Varchar (30) NOT NULL ,
        textdisplay            Varchar (600) NOT NULL ,
        likecount              number(19) NOT NULL ,
        auteur_profile_image   Varchar (150) NOT NULL ,
        comment_id_Commentaire Varchar (60) NOT NULL ,
        ID_video               Varchar (50) NOT NULL ,
        id_channel             Varchar (50) NOT NULL
	,CONSTRAINT Commentaire_PK PRIMARY KEY (comment_id)
);

ALTER TABLE Video
	ADD CONSTRAINT Video_Channel0_FK
	FOREIGN KEY (id_channel)
	REFERENCES Channel(id_channel);

ALTER TABLE Resultat
	ADD CONSTRAINT Conserner_Video0_FK
	FOREIGN KEY (ID_video)
	REFERENCES Video(ID_video);

ALTER TABLE Resultat
	ADD CONSTRAINT Conserner_Recherche1_FK
	FOREIGN KEY (Id_recherche)
	REFERENCES Recherche(Id_recherche);

ALTER TABLE Commentaire
	ADD CONSTRAINT Commentaire_Commentaire0_FK
	FOREIGN KEY (comment_id_Commentaire)
	REFERENCES Commentaire(comment_id);

ALTER TABLE Commentaire
	ADD CONSTRAINT Commentaire_Video1_FK
	FOREIGN KEY (ID_video)
	REFERENCES Video(ID_video);

ALTER TABLE Commentaire
	ADD CONSTRAINT Commentaire_Channel2_FK
	FOREIGN KEY (id_channel)
	REFERENCES Channel(id_channel);
    
select * from recherche;
select * from video ; 
select * from channel; 
select * from commentaire; 
select * from Resultat; 



/* sql */
--1 Pour chaque query (query,id,titre) de la video qui a obtenu le max de like :
select r.query , v.id_video , v.Titre 
from video v , recherche r , resultat res
where v.id_video = res.id_video and res.id_recherche = r.id_recherche
and v.likecount>=all(
					 select likecount 
					 from video vi, resultat re 
					 where  vi.id_video = re.id_video
					 and res.id_recherche = re.id_recherche
					 );

--2 Pour chaque query (query,id,titre) de la video qui obtenu le max de dislike :
select r.query , v.id_video , v.Titre 
from video v , recherche r , resultat res
where v.id_video = res.id_video and res.id_recherche = r.id_recherche
and v.dislikecount>=all(
					 select dislikecount 
					 from video vi, resultat re 
					 where  vi.id_video = re.id_video
					 and res.id_recherche = re.id_recherche
					 );
					 
--3 Parmi les videos combien de video ont été publiées par année et par query
select r.query ,v.publishedAt as videoPablishedAt ,count(v.id_video) 
from video v , recherche r , resultat res
where v.id_video = res.id_video and res.id_recherche = r.id_recherche
group by (v.publishedAt , r.query);  



--4 Pour chaque video (count(comment_id ,count(id_channel))
select v.id_video , count(c.comment_id) as Total_comment
from video v join commentaire c on v.id_video = c.id_video
group by (v.id_video);

--5 = 1



--6 Pour chaque query (query,id,titre) de la video qui a obtenu le max de commentaire :
select r.query , v.id_video , v.Titre 
from video v , recherche r , resultat res
where v.id_video = res.id_video and res.id_recherche = r.id_recherche
and v.commentcount>=all(
					 select commentcount 
					 from video vi, resultat re 
					 where  vi.id_video = re.id_video
					 and res.id_recherche = re.id_recherche
					 );
--7 Pour une requete , (id_channel,titre_channel) de l'utilisateur qui redigé le commentaire le plus populaire
select c.id_channel , c.auteur_name 
from (	select r.query , v.id_video , v.Titre 
		from video v , recherche r , resultat res
		where v.id_video = res.id_video and res.id_recherche = r.id_recherche
		and v.likecount>=all(
							 select likecount 
							 from video vi, resultat re 
							 where  vi.id_video = re.id_video
							 and res.id_recherche = re.id_recherche
							 )
	) vl, commentaire c
where vl.id_video = c.id_video 
and c.likecount >= all(select likecount 
					   from  commentaire c1			
					   where vl.id_video = c1.id_video);


					   
					   
					   