USE [BAZA]
GO

/****** Object:  Trigger [dbo].[TR_TransportOffer_ObrisiPonude]    Script Date: 6/20/2024 3:07:16 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[TR_TransportOffer_ObrisiPonude]
   ON  [dbo].[Voznja]
   AFTER INSERT
AS 
BEGIN
	
	declare @SifraPaketa int
	declare @kursor cursor

	set @kursor = cursor for
	select SifP from inserted

	open @kursor

	fetch next from @kursor
	into @SifraPaketa

	while @@FETCH_STATUS = 0
	begin
		
		delete from Ponuda
		where Ponuda.SifPaket = @SifraPaketa
		
		fetch next from @kursor
		into @SifraPaketa
	end

	close @kursor
	deallocate @kursor

END
GO

ALTER TABLE [dbo].[Voznja] ENABLE TRIGGER [TR_TransportOffer_ObrisiPonude]
GO

