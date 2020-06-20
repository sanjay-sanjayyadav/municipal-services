ALTER TABLE public.material ADD COLUMN materialclass character varying(255);
ALTER TABLE public.material ADD COLUMN description character varying(255);
ALTER TABLE public.material ADD COLUMN inactivedate bigint;
ALTER TABLE public.material ADD COLUMN oldcode character varying(50);
ALTER TABLE public.material ADD COLUMN materialtype character varying(50);
ALTER TABLE public.material ADD COLUMN baseuom character varying(50);
ALTER TABLE public.material ADD COLUMN inventorytype character varying(50);
ALTER TABLE public.material ADD COLUMN status boolean;
ALTER TABLE public.material ADD COLUMN purchaseuom character varying(50);
ALTER TABLE public.material ADD COLUMN expenseaccount character varying(50);
ALTER TABLE public.material ADD COLUMN stockinguom character varying(50);
ALTER TABLE public.material ADD COLUMN lotcontrol boolean;
ALTER TABLE public.material ADD COLUMN shelflifecontrol boolean;
ALTER TABLE public.material ADD COLUMN serialnumber boolean;
ALTER TABLE public.material ADD COLUMN scrapable boolean;
ALTER TABLE public.material ADD COLUMN assetcategory character varying(150);
ALTER TABLE public.material ADD COLUMN model character varying(100);
ALTER TABLE public.material ADD COLUMN manufacturepartno character varying(255);
ALTER TABLE public.material ADD COLUMN techincalspecs character varying(255);
ALTER TABLE public.material ADD COLUMN termsofdelivery character varying(255);