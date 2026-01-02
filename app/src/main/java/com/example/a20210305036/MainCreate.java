package com.example.a20210305036;
import androidx.annotation.NonNull;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.Toast;
import java.util.Calendar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.ArrayList;



public class MainCreate extends AppCompatActivity {
    EditText etListName, etListDate, etNewItem;
    TextView tvCategorySelect;
    Button btnSave, btnAddItem;
    DataBaseHelper dbHelper;
    String selectedCategory = "";

    RecyclerView rvItems;
    ArrayList<String> tempItemList;
    SimpleItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_create);
        etListName = findViewById(R.id.etListName);
        etListDate = findViewById(R.id.etListDate);
        tvCategorySelect = findViewById(R.id.tvCategorySelect);
        btnSave = findViewById(R.id.btnSave);

        etNewItem = findViewById(R.id.etNewItem);
        btnAddItem = findViewById(R.id.btnAddItem);
        rvItems = findViewById(R.id.rvItems);

        dbHelper = new DataBaseHelper(this);

        tempItemList = new ArrayList<>();
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleItemAdapter(tempItemList);
        rvItems.setAdapter(adapter);


        etListDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePicker = new DatePickerDialog(MainCreate.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etListDate.setText(formattedDate);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });


        tvCategorySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryMenu();
            }
        });


        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemText = etNewItem.getText().toString();
                if (!itemText.isEmpty()) {
                    tempItemList.add(itemText);
                    adapter.notifyDataSetChanged();
                    etNewItem.setText("");
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etListName.getText().toString();
                String date = etListDate.getText().toString();

                if (name.isEmpty() || date.isEmpty() || selectedCategory.isEmpty()) {
                    Toast.makeText(MainCreate.this, "Please fill all header fields!", Toast.LENGTH_SHORT).show();
                } else {

                    long listId = dbHelper.addList(name, date, selectedCategory);

                    if (listId != -1) {

                        for (String itemName : tempItemList) {
                            dbHelper.addItem(listId, itemName);
                        }

                        Toast.makeText(MainCreate.this, "List Saved Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MainCreate.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    private void showCategoryMenu() {

        PopupMenu popup = new PopupMenu(MainCreate.this, tvCategorySelect);


        popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedCategory = item.getTitle().toString();
                tvCategorySelect.setText(selectedCategory);
                return true;
            }
        });
        popup.show();
    }

    class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.ViewHolder> {
        ArrayList<String> data;

        public SimpleItemAdapter(ArrayList<String> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(data.get(position));
            holder.cbDone.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            View cbDone;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvItemRowName);
                cbDone = itemView.findViewById(R.id.cbItemDone);
            }
        }
    }
}