USE [BAZA]
GO

/****** Object:  StoredProcedure [dbo].[SPGrant_Request]    Script Date: 6/20/2024 3:09:02 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[SPGrant_Request]
	@userName varchar(100),
	@success int OUTPUT
AS
BEGIN
	DECLARE @SifK int
    DECLARE @SifV int 

    
    SELECT @SifK = Korisnik.SifK 
    FROM Korisnik join ZahtevZaKurira on(Korisnik.SifK = ZahtevZaKurira.SifK)
    WHERE Kor_ime = @userName

    IF (@SifK IS NULL)
    BEGIN
        SET @success = 0  
        RETURN  
    END

	select @SifV = SifV from ZahtevZaKurira
	where SifK = @SifK

    IF (@SifV IS NULL)
    BEGIN
        SET @success = 0  
        RETURN  
    END

    IF EXISTS (select * FROM Kurir WHERE SifV = @SifV)
    BEGIN
		delete from ZahtevZaKurira
		where SifK = @SifK
        SET @success = 0  
        RETURN  
    END

    insert into Kurir(SifK,SifV) values(@SifK,@SifV)

	delete from ZahtevZaKurira
	where SifK = @SifK


    SET @success = 1 
END
GO

