package com.example.pc_builder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {
    List<Integer> idList = new ArrayList<>();
    int currentCpuId=0;
    int currentGpuId=0;
    int currentMBId=0;
    int currentCpufanId=0;
    int currentSsdId=0;
    int currentPsuId=0;
    int currentRamId=0;
    int unspentBudget=0;
    String cpuName="";
    String ramName="";
    String gpuName="";
    String mbName="";
    String cpufanName="";
    String ssdName="";
    String psuName="";
    int currentPrice=0;
    int bploh=0;
int memoryNumber=0;
String chooseCpu="";
float forGameAchzCpu=0;
float forGameAchzGpu=0;
int forGameRam=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data data = new Data(this);
        data.UpdateData();
        setContentView(R.layout.activity_selection);
        Button selectButton = findViewById(R.id.btSelection);
        EditText edt_price = findViewById(R.id.edt_price);
        TextView tv_cpu = findViewById(R.id.tv_cpu);
        TextView tv_gpu = findViewById(R.id.tv_gpu);
        TextView tv_mb = findViewById(R.id.tv_mb);
        TextView tv_cpufan = findViewById(R.id.tv_cpufan);
        TextView tv_ssd = findViewById(R.id.tv_ssd);
        TextView tv_psu = findViewById(R.id.tv_psu);
        TextView tv_ram = findViewById(R.id.tv_ram);
        TextView tv_chmo = findViewById(R.id.tv_chmo);
        TextView tv_price=findViewById(R.id.tv_price);
        gameAdapter adapter = new gameAdapter (getApplicationContext());
        selectButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                currentCpuId=0;
                currentGpuId=0;
                currentMBId=0;
                currentCpufanId=0;
                currentSsdId=0;
                currentPsuId=0;
                currentRamId=0;
                cpuName="";
                ramName="";
                gpuName="";
                mbName="";
                cpufanName="";
                ssdName="";
                psuName="";
                currentPrice=0;
                memoryNumber=0;
                chooseCpu="";
                int totalPrice=0;
                int budget =0;
                Spinner cpuChoose= (Spinner)findViewById(R.id.spr_cpuChoose);
                String selectedItem = cpuChoose.getSelectedItem().toString();
                if (selectedItem.equals("Intel")) {
                    chooseCpu="Intel";
                } else if (selectedItem.equals("AMD")) {
                    chooseCpu = "AMD";
                }
                else
                {
                    chooseCpu = "Все";
                }


                try{
                    budget= Integer.parseInt(edt_price.getText().toString());
                }
                catch (Exception e)
                {
                    return;
                }

                if (budget<23000) {
                    edt_price.setHint("Минимум 23000");
                    edt_price.setText("");
                    tv_cpu.setText("");
                    tv_gpu.setText("");
                    tv_mb.setText("");
                    tv_cpufan.setText("");
                    tv_ssd.setText("");
                    tv_psu.setText("");
                    tv_ram.setText("");
                    tv_chmo.setText("");
                    tv_price.setText("");
                    adapter.clearData();
                    return;
                }

                edt_price.setHint("Введите сумму");
                int processorBudget = (int) (budget * 0.17);
                Data data =new Data(getApplicationContext());
                try {


                    //Процессор--------------------------------------------------------------------------
                    Cursor cursor = data.getCpu(processorBudget, chooseCpu);
                    if (cursor != null && cursor.moveToFirst()) {
                        float constantMaxGach = 0;
                        float currentMaxGach = 0;
                        float priceQuality = 0;
                        float maxPriceQuality = 0;
                        constantMaxGach = cursor.getFloat(5);
                        do {
                            int id = cursor.getInt(0);
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);

                            float gachz = cursor.getFloat(5);
                            if (gachz >= constantMaxGach * 0.7) {
                                if (currentMaxGach == 0 || currentMaxGach < gachz) {
                                    currentMaxGach = gachz;
                                    priceQuality = gachz / price;
                                    if (maxPriceQuality < priceQuality) {
                                        maxPriceQuality = priceQuality;
                                        currentCpuId = id;
                                        cpuName = name;
                                        currentPrice = price;
                                        forGameAchzCpu=gachz;
                                    }
                                }
                            }
                        }
                        while (cursor.moveToNext());
                    }

                    totalPrice += currentPrice;
                    unspentBudget=processorBudget-currentPrice;
                    tv_cpu.setText(cpuName.toString());
                    currentPrice = 0;

//Видеокарта--------------------------------------------------------------------------
                    int graphicsCardBudget = (int) (budget * 0.424+unspentBudget);
                    unspentBudget=0;
                    cursor = data.getGpu(graphicsCardBudget);

                    if (cursor != null && cursor.moveToFirst() && currentCpuId!=0) {
                        float constantMaxGach = 0;
                        float currentMaxGach = 0;
                        float priceQuality = 0;
                        float maxPriceQuality = 0;
                        constantMaxGach = cursor.getFloat(5);
                        do {
                            int id = cursor.getInt(0);
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);

                            float gachz = cursor.getFloat(5);
                            if (gachz >= constantMaxGach * 0.95) {
                                if (currentMaxGach == 0 || currentMaxGach < gachz) {
                                    currentMaxGach = gachz;
                                }

                                priceQuality = gachz / price;
                                if (maxPriceQuality < priceQuality) {
                                    maxPriceQuality = priceQuality;
                                    currentGpuId = id;
                                    gpuName = name;
                                    currentPrice = price;
                                    forGameAchzGpu=gachz;
                                }
                            } else
                                break;
                        }
                        while (cursor.moveToNext());
                    }
                    totalPrice += currentPrice;
                    unspentBudget=graphicsCardBudget-currentPrice;
                    tv_gpu.setText(gpuName.toString());
                    currentPrice = 0;
                    //материнка--------------------------------------------------------------------------
                    int motherboardBudget = (int) (budget * 0.144+unspentBudget);
                    unspentBudget=0;
                    cursor = data.getMotherboard(motherboardBudget, currentCpuId);
                    if (cursor != null && cursor.moveToFirst()) {
                        float maxPrice = 0;
                        do {
                            int id = cursor.getInt(0);
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);
                            if (maxPrice == 0 || price > maxPrice) {
                                maxPrice = price;
                                currentMBId = id;
                                mbName = name;
                                currentPrice = price;
                            }

                        }
                        while (cursor.moveToNext());
                    }
                    totalPrice += currentPrice;
                    unspentBudget=motherboardBudget-currentPrice;
                    tv_mb.setText(mbName.toString());
                    currentPrice = 0;
                    //охлад--------------------------------------------------------------------------
                    int cpufanBudget = (int) (budget * 0.033+unspentBudget);
                    unspentBudget=0;
                    cursor = data.getCpufan(cpufanBudget, currentCpuId);
                    if (cursor != null && cursor.moveToFirst()) {
                        float maxCpuFanTdp = 0;
                        float currentCpuFanTdp = 0;
                        do {
                            int id = cursor.getInt(0);
                            currentCpuFanTdp = Float.parseFloat(data.getCpuFanTdp(id));
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);
                            if (maxCpuFanTdp == 0 || currentCpuFanTdp > maxCpuFanTdp) {
                                maxCpuFanTdp = currentCpuFanTdp;
                                currentCpufanId = id;
                                cpufanName = name ;
                                currentPrice = price;
                            }
                        }
                        while (cursor.moveToNext());
                    }
                    totalPrice += currentPrice;
                    unspentBudget=cpufanBudget-currentPrice;
                    tv_cpufan.setText(cpufanName.toString());
                    currentPrice = 0;
                    //ssd--------------------------------------------------------------------------
                    int ssdBudget = (int) (budget * 0.065+unspentBudget);
                    unspentBudget=0;
                    cursor = data.getSsd(ssdBudget);
                    if (cursor != null && cursor.moveToFirst() && currentCpuId!=0) {
                        float maxMemoryNumber = 0;
                        float currentMemoryNumber = 0;
                        do {
                            currentMemoryNumber = Float.parseFloat(cursor.getString(5));
                            int id = cursor.getInt(0);
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);
                            if (maxMemoryNumber == 0 || currentMemoryNumber > maxMemoryNumber) {
                                maxMemoryNumber = currentMemoryNumber;
                                currentSsdId = id;
                                ssdName = name + " " + currentMemoryNumber + " ГБ";
                                currentPrice = price;
                            }

                        }
                        while (cursor.moveToNext());
                    }
                    totalPrice += currentPrice;
                    unspentBudget=ssdBudget-currentPrice;
                    tv_ssd.setText(ssdName.toString());
                    currentPrice = 0;
                    //блок питания--------------------------------------------------------------------------
                    int psuBudget = (int) (budget * 0.069+unspentBudget);
                    unspentBudget=0;
                    cursor = data.getPsu(psuBudget, currentCpuId, currentGpuId);
                    if (cursor != null && cursor.moveToFirst())
                    {
                        float maxCertificate = 0;
                        float currentCertificate = 0;
                        do {
                            int id = cursor.getInt(0);
                            currentCertificate = Float.parseFloat(data.getCertificate(id));
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);
                            if (maxCertificate == 0 || currentCertificate > maxCertificate) {
                                maxCertificate = currentCertificate;
                                currentPsuId = id;
                                psuName = name ;
                                currentPrice = price;
                            }
                        }
                        while (cursor.moveToNext());
                    }

                    totalPrice += currentPrice;
                    unspentBudget=psuBudget-currentPrice;
                    tv_psu.setText(psuName.toString());
                    currentPrice = 0;
//Оперативка--------------------------------------------------------------------------
                    int ramBudget = (int) (budget * 0.095+unspentBudget);
                    unspentBudget=0;
                    ramBudget /= 2;
                    cursor = data.getRam(ramBudget, currentMBId);
                    if (cursor.getCount() == 0) {
                        ramBudget *= 2;
                        cursor = data.getRam(ramBudget, currentMBId);
                        memoryNumber = 1;
                    } else {
                        memoryNumber = 2;
                    }

                    if (cursor != null && cursor.moveToFirst()) {
                        float maxFrequency = 0;
                        float currentFrequency = 0;
                        maxFrequency = cursor.getFloat(5);
                        do {
                            int id = cursor.getInt(0);
                            currentFrequency = Float.parseFloat(data.getFrequency(id));
                            String name = cursor.getString(1);
                            int price = cursor.getInt(3);
                            if (maxFrequency == 0 || currentFrequency > maxFrequency) {
                                maxFrequency = currentFrequency;
                                currentRamId = id;
                                ramName = name + " " + data.getRamMemoryValue(id) + " ГБ";
                                if (memoryNumber == 1) {
                                    currentPrice = price;
                                    forGameRam=Integer.parseInt(data.getRamMemoryValue(id));
                                } else {
                                    currentPrice = price * 2;
                                    forGameRam=Integer.parseInt(data.getRamMemoryValue(id))*2;
                                }
                            }
                        }
                        while (cursor.moveToNext());
                    }

                    totalPrice += currentPrice;
                    tv_ram.setText(ramName.toString());
                    tv_chmo.setText("X" + memoryNumber);
                    currentPrice = 0;

                    //Вывод игр-----------------------------------------------------------------------
                    adapter.clearData();
                    RecyclerView recyclerView = findViewById(R.id.recycler);


                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);

                    cursor = data.getGameList(forGameAchzCpu,forGameAchzGpu,forGameRam);

                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(0);
                            String GameName = cursor.getString(1);
                            int CpuScore = cursor.getInt(2);
                            int GpuScore = cursor.getInt(3);
                            int Ram = cursor.getInt(4);
                            String ImagePath = cursor.getString(5);
                            Game game = new Game (id, GameName,CpuScore,GpuScore,Ram,ImagePath);
                            adapter.addData(game );
                        } while (cursor.moveToNext());
                    }
                    //-----------------------------------------------------------------------
                    tv_price.setText(String.valueOf(totalPrice));
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                    Toast.makeText(getApplicationContext(),"При таких параметрах сборка невозможна", Toast.LENGTH_LONG).show();

                    tv_cpu.setText("");
                    tv_gpu.setText("");
                    tv_mb.setText("");
                    tv_cpufan.setText("");
                    tv_ssd.setText("");
                    tv_psu.setText("");
                    tv_ram.setText("");
                    tv_chmo.setText("");
                    tv_price.setText("");
                    adapter.clearData();
                }


            }


        });

    }

    public void toMinexc(View view){
        if(currentCpuId != 0){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://minexc.ru/pcBuild?gpuId="+currentGpuId+"&cpuId="+currentCpuId+"&motherboardId="+currentMBId+"&ramId="+currentRamId+"&countRam="+memoryNumber+"&storageId="+currentSsdId+"&psuId="+currentPsuId+"&coolerId="+currentCpufanId)));
        }else{
            Toast.makeText(this, "Комплектующие не подобраны", Toast.LENGTH_SHORT).show();
        }
    }

}




