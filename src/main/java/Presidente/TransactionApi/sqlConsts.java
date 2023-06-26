package Presidente.TransactionApi;

public class sqlConsts {
	
	//ApiUuidStatusFile
	//
	static String sqlApiUuidStatus = "SELECT count(transaction_id) FROM public.transactions where status = 0 and transaction_time > '2023-02-19'";
	static String[] columnsApiUuidStatus = { "count" };
	
	//App 
	//
	static String sqlGetJsonWithStatus0 = "SELECT public.get_json_by_status(0)";
	static String[] columnsGetJsonWithStatus = { "get_json_by_status" };
	
	//badTransaction
	//
	static String sqlBadTransaction = "SELECT bed_transactions_id FROM public.bad_transactions where status = 0";
	static String[] columnBadTransaction = { "bed_transactions_id" };
	
	//Check
	//
	static String sqlGetJsonWithStatus11 = "SELECT public.get_json_by_status(11)";
	
	//GetMachineMacAdress
	//
	static String sglGetMacAdressByStickerNumber = "SELECT id_number FROM public.machines WHERE sticker_number =";
	static String[] columnsGetMacAdressByStickerNumber = { "id_number" };
	
	//GetSlotPeriodicWithStatus0
	//
	static String sqlGetSlotPeriodicWithStatus0 = "SELECT public.get_json_sp_by_status(0)";
	static String [] columnGetSlotPeriodicWithStatus0 = {"get_json_sp_by_status"};
	
	//GetSlotPeriodicWithStatus0
	//
	static String sqlGetCronError = "SELECT public.get_sp_cron_job_error_counter()";
	static String [] columnGetCronError = {"get_sp_cron_job_error_counter"};
	
	//LocationWorkCheck
	//
	static String sqlLocationWorkCheck = "select slot_club_id from slot_clubs where not slot_club_id in (SELECT distinct slot_club_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW()) AND work_status = true";
	static String[] columnsLocationWorkCheck = {"slot_club_id"};
	
	//paymentCheck 
	//
	static String sqlPaymentCheck = "SELECT SUM(public.transactions.transaction_amount) as suma, count(public.transactions.transaction_id) as brojUplata, public.transactions.slot_club_id, public.transaction_types.path FROM public.transactions INNER JOIN public.transaction_types ON public.transactions.transaction_types = public.transaction_types.transaction_types WHERE public.transactions.transaction_time BETWEEN CURRENT_DATE - INTEGER '1' + TIME '06:45:59' AND CURRENT_DATE + TIME '03:59:59' GROUP BY public.transactions.slot_club_id, public.transaction_types.path";
	static String[] columnsPaymentCheck = { "suma", "slot_club_id", "brojUplata", "path" };
	
	//shitHapend
	//
	static String sqlShitsHapend = "SELECT COUNT(*) FROM transactions WHERE transaction_time >= NOW() - INTERVAL '25 minutes';";
	static String sqlShitsHapend1 = "SELECT COUNT(*) FROM transactions WHERE transaction_time >= NOW() - INTERVAL '25 minutes' and status = 0;";
	static String[] columnsShitsHapend = { "count" };
	
	//slotPeriodicCheck
	//
	static String sqlSlotPeriodicCheck = "SELECT api_json FROM public.slot_periodic_h ORDER BY report_index DESC LIMIT 1";
	static String[] columnsSlotPeriodicCheck = { "api_json" };
	
	//statusChecker
	//
	static String sqlStatusChecker = "CALL public.transaction_status_10_update_to_1()";
	
	//slot club data
	//
	static String sqlSlotClubDataWithSid = "SELECT * FROM public.slot_clubs where slot_club_sid =";
	static String sqlSlotClubDataWithId = "SELECT * FROM public.slot_clubs where slot_club_id =";
	static String[] columnsSlotClubData = {"slot_club_id", "adresa", "opstina", "mesto", "slot_club_sid"};
	
}
