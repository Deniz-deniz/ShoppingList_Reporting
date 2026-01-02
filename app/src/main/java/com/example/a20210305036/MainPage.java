package com.example.a20210305036;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import android.content.DialogInterface;

public class MainPage extends AppCompatActivity {

    ListView listView;
    DataBaseHelper dbHelper;

    ArrayList<String> displayList;
    ArrayList<Integer> listIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        listView = findViewById(R.id.listViewShoppingLists);
        dbHelper = new DataBaseHelper(this);

        displayList = new ArrayList<>();
        listIds = new ArrayList<>();


        loadLists();
        registerForContextMenu(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int tiklananId = listIds.get(position);
                String tiklananIsim = displayList.get(position);

                showListItemsDialog(tiklananId, tiklananIsim);
            }
        });
    }


    private void loadLists() {
        displayList.clear();
        listIds.clear();

        Cursor cursor = dbHelper.getAllLists();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Listeniz boş, yeni ekleyin!", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                // Sütunlar: 0:ID, 1:NAME, 2:DATE, 3:CATEGORY
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String date = cursor.getString(2);
                String category = cursor.getString(3);

                listIds.add(id);

                displayList.add(name + "\n" + date + " (" + category + ")");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }


    private void showListItemsDialog(long listId, String listTitle) {

        Cursor cursor = dbHelper.getListItems(listId);

       //Lists
        ArrayList<String> urunIsimleri = new ArrayList<>();
        ArrayList<Boolean> urunDurumlari = new ArrayList<>();
        ArrayList<Integer> urunIdleri = new ArrayList<>();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Bu liste boş.", Toast.LENGTH_SHORT).show();
            return;
        }


        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(2);
            int status = cursor.getInt(3);

            urunIdleri.add(id);
            urunIsimleri.add(name);
            urunDurumlari.add(status == 1);
        }


        String[] itemArray = urunIsimleri.toArray(new String[0]);
        boolean[] statusArray = new boolean[urunDurumlari.size()];
        for(int i=0; i<urunDurumlari.size(); i++) statusArray[i] = urunDurumlari.get(i);


        AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        builder.setTitle(listTitle.split("\n")[0]);


        builder.setMultiChoiceItems(itemArray, statusArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {


                int tiklananUrunId = urunIdleri.get(which);


                dbHelper.updateItemStatus(tiklananUrunId, isChecked);
            }
        });

        builder.setPositiveButton("TAMAM", null);
        builder.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        menu.setHeaderTitle("Seçenekler");
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        if (item.getItemId() == R.id.action_delete) {

            int silinecekId = listIds.get(info.position);


            dbHelper.deleteList(silinecekId);


            loadLists();
            Toast.makeText(this, "Liste Silindi!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
