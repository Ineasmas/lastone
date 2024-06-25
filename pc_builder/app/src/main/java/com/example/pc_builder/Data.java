package com.example.pc_builder;
import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
public class Data extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "local.db";
    private static final int DATABASE_VERSION = 1;
    private static Context cont;

    private DataUpdateListener listener;

    public Data(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        cont = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void setOnDataUpdateListener(DataUpdateListener listener) {
        this.listener = listener;
    }
    public DataUpdateListener getListener() {
        return listener;
    }
    public interface DataUpdateListener {
        void onDataUpdateComplete();
    }
    public void UpdateData() {
        deleteTable("Components");
        deleteTable("Properties");
        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Roma/select";

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                Gson gson = new Gson();
                responseProductData[] resp = gson.fromJson(json, responseProductData[].class);
                SQLiteDatabase db = cont.openOrCreateDatabase("local.db", MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS Components (id INTEGER, Name TEXT , typeName TEXT, Price INTEGER)");
                db.execSQL("CREATE TABLE IF NOT EXISTS Properties (idComponents INTEGER, Name TEXT, Value TEXT)");
                for(int i = 0; i < resp.length; i++){
                    String exception = String.format("INSERT INTO Components (id, Name, typeName, Price) VALUES (%s, '%s', '%s', %s)", resp[i].id, resp[i].Name, resp[i].typeName, resp[i].Price);
                    db.execSQL(exception);

                    if (resp[i].properties != null && !resp[i].properties.isEmpty()) {
                        for (properties hotkak : resp[i].properties) {
                            // вставка данных в таблицу
                            String productListInsertQuery = String.format("INSERT INTO Properties (idComponents, Name, Value) VALUES (%s, '%s', '%s')",
                                   resp[i].id , hotkak.Name, hotkak.Value);
                            db.execSQL(productListInsertQuery);
                        }
                    }
                }
                UpdateGame();
            }
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });
    }
    public void UpdateGame() {
        deleteTable("Game");
        OkHttpClient client = new OkHttpClient();
        String url = "https://minexc.ru/Roma/selectGame";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();
                Handler handler = new Handler(Looper.getMainLooper());
                Gson gson = new Gson();
                responseGame[] resp = gson.fromJson(json, responseGame[].class);
                SQLiteDatabase db = cont.openOrCreateDatabase("local.db", MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE IF NOT EXISTS Game (id INTEGER, GameName TEXT , CpuScore INTEGER, GpuScore INTEGER,Ram INTEGER,ImagePath TEXT)");
                for(int i = 0; i < resp.length; i++){
                    String exception = String.format("INSERT INTO Game (id, GameName, CpuScore, GpuScore, Ram, ImagePath) VALUES (%s, '%s', %s, %s, %s, '%s')", resp[i].id, resp[i].GameName, resp[i].CpuScore, resp[i].GpuScore, resp[i].Ram,resp[i].ImagePath);
                    db.execSQL(exception);
                }
                db.close();
            }
            @Override
            public void onFailure(Call call, IOException e) {
                String error = e.toString();
            }
        });
    }

    public class responseProductData{
        private int id;
        private String Name;
        private float Price;
        private String typeName;
        private List<properties> properties;
    }

    public class responseGame{
        private int id;
        private String GameName;
        private int CpuScore;
        private int GpuScore;
        private int Ram;
        private String ImagePath;

    }
    public class properties{
        private String Name;
        private String Value;
    }
    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        db.close();
        return false;
    }
    public void deleteTable(String nameTable){

        if(isTableExists(nameTable)){
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("DROP TABLE " + nameTable);
            db.close();
        }
    }
    public Cursor getCpu(int processorBudget,String chooseCpu){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query="";
            if (chooseCpu=="Intel")
            {
                query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <= "  + processorBudget + "  AND typeName = 'Процессор' AND Properties.Name = 'АЧЗ' AND Components.Name LIKE 'Intel%'  ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            }
            if (chooseCpu=="AMD") {
                query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <= "   + processorBudget + "  AND typeName = 'Процессор' AND Properties.Name = 'АЧЗ' AND Components.Name LIKE 'AMD%' ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            }
            if (chooseCpu=="Все") {
                query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents  WHERE Price <= "   + processorBudget + "  AND typeName = 'Процессор' AND Properties.Name = 'АЧЗ' ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            }
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public Cursor getGpu(int graphicsCardBudget){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id,Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <= "  + graphicsCardBudget + " AND typeName = 'Видеокарта' AND Properties.Name = 'АЧЗ' ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public Cursor getMotherboard(int motherboardBudget, int cpuId){
        if (isTableExists("Components") && isTableExists("Properties")) {
          SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <= " + motherboardBudget + "  AND typeName = 'Материнская плата' AND Properties.Value ='" + getSocket(cpuId) +"' ORDER BY id DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }
    public Cursor getCpufan(int cpufanBudget, int cpuId){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <=  " + cpufanBudget + " AND typeName = 'Охлаждение процессора' AND Properties.Value >= " + getCpuTDP(cpuId) + " AND Properties.Value = '" + getSocket(cpuId) + "' ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }
    public Cursor getSsd(int ssdBudget){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <=  " + ssdBudget+ " AND typeName = 'Накопитель данных' AND Properties.Name = 'Объем памяти' ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public Cursor getPsu(int psuBudget, int cpuId, int gpuId){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            double totalTDP = (Double.parseDouble(getCpuTDP(cpuId)) +100+ Double.parseDouble(getGpuEnergyСonsumption(gpuId))) * 1.1;
            String query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <= " + psuBudget + " AND typeName = 'Блок питания' AND Properties.Name = 'Мощность' AND Properties.Value >= " + totalTDP + " ORDER BY Properties.Value DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public Cursor getRam(int ramBudget, int motherboardId){
        if (isTableExists("Components") && isTableExists("Properties")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, Components.Name, typeName, Price, Properties.Name, Properties.Value FROM Components INNER JOIN Properties ON Components.id = Properties.idComponents WHERE Price <=  " + ramBudget + "  AND typeName = 'Оперативная память' AND Properties.Name = 'Тип памяти' AND Properties.Value ='" + getMemorytype(motherboardId) +"' ORDER BY id DESC LIMIT 10 OFFSET 0";
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public Cursor getGameList(float  achzCpu,float achzGpu, int ram){
        if (isTableExists("Game")) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT id, GameName, CpuScore, GpuScore, Ram, ImagePath FROM Game  WHERE CpuScore <=  " + achzCpu+ " AND GpuScore <=  " + achzGpu+ " AND Ram <=  " + ram;
            return db.rawQuery(query, null);
        } else {
            return null;
        }
    }

    public String getSocket(int idProcessor){
        String socket = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Сокет' and idComponents= "+ idProcessor, null);

            if(cursor != null && cursor.moveToFirst()){
                socket = cursor.getString(0);
            }

            return socket;
        }
        return socket;
    }
    public String getCpuTDP(int idProcessor){
        String tdp = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Тепловыделение' and idComponents= "+ idProcessor, null);

            if(cursor != null && cursor.moveToFirst()){
                tdp= cursor.getString(0);
            }

            return tdp;
        }
        return tdp;
    }

    public String getGpuEnergyСonsumption(int idGraphicCard) {//энергопотребление
        String EnergyСonsumption = "";
        if (isTableExists("Properties")) {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Энергопотребление' and idComponents= " + idGraphicCard, null);

            if (cursor != null && cursor.moveToFirst()) {
                EnergyСonsumption = cursor.getString(0);
            }

            return EnergyСonsumption;
        }
        return EnergyСonsumption;
    }

    public String getMemorytype(int idmMotherboard) {//ddr
        String memoryType = "";
        if (isTableExists("Properties")) {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Поддерживаемая оперативная память' and idComponents= " + idmMotherboard, null);

            if (cursor != null && cursor.moveToFirst()) {
                memoryType = cursor.getString(0);
            }

            return memoryType;
        }
        return memoryType;
    }

    public String getRamMemoryValue(int idRam){
        String memoryValue = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Объем памяти' and idComponents= "+ idRam, null);

            if(cursor != null && cursor.moveToFirst()){
                memoryValue = cursor.getString(0);
            }

            return memoryValue;
        }
        return memoryValue;
    }

    public String getFrequency(int idRam){
        String ramFrequency = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Частота' and idComponents= "+ idRam, null);

            if(cursor != null && cursor.moveToFirst()){
                ramFrequency = cursor.getString(0);
            }

            return ramFrequency;
        }
        return ramFrequency;
    }

    public String getCertificate(int idPsu){
        String certificate = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Тип сертификата 80+' and idComponents= "+ idPsu, null);

            if(cursor != null && cursor.moveToFirst()){
                certificate = cursor.getString(0);
            }

            return certificate;
        }
        return certificate;
    }
    public String getCpuFanTdp(int idCpuFan){
        String сpuFanTdp = "";
        if(isTableExists("Properties")){

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Value FROM Properties WHERE Properties.Name='Рассеиваемая мощность' and idComponents= "+ idCpuFan, null);

            if(cursor != null && cursor.moveToFirst()){
                сpuFanTdp = cursor.getString(0);
            }

            return сpuFanTdp;
        }
        return сpuFanTdp;
    }


}