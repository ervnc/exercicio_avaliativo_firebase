// Dupla: Evandro e Laura

package com.example.teste_aula14_10_222;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference BD = FirebaseDatabase.getInstance().getReference();

    // ===========================
    // Criando o nó "restaurante"
    // ===========================
    DatabaseReference restaurante = BD.child("restaurante");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText txtMesa = findViewById( R.id.txtMesa );
        EditText txtItem = findViewById( R.id.txtItem );
        EditText txtProduto = findViewById( R.id.txtProduto );
        EditText txtPreco = findViewById( R.id.txtPreco );
        Button btnInserir = findViewById( R.id.btnInserir );
        Button btnListar = findViewById( R.id.btnListar );
        Button btnCalcular = findViewById( R.id.btnCalcular );
        Button btnZerar = findViewById( R.id.btnZerar );
        EditText txtMesaCoz = findViewById( R.id.txtMesaCoz );
        EditText txtItemCoz = findViewById( R.id.txtItemCoz );
        Button btnAtender = findViewById( R.id.btnAtender );

        // ==============
        // Botão Inserir
        // ==============
        btnInserir.setOnClickListener(view -> {
            String mesa, item, produto;
            double preco;

            mesa = txtMesa.getText().toString();
            item = txtItem.getText().toString();
            produto = txtProduto.getText().toString();
            preco = Double.parseDouble( txtPreco.getText().toString() );

            // Criando o construtor e adicionando os child no nó restaurante
            Item i = new Item( produto, preco );
            restaurante.child(mesa).child(item).setValue(i);

            // Limpando os inputs
            txtMesa.setText(null);
            txtItem.setText(null);
            txtProduto.setText(null);
            txtPreco.setText(null);
        });


        // =============
        // Botão Listar
        // =============
        btnListar.setOnClickListener(view -> {
            String mesa;
            mesa = txtMesa.getText().toString();
            restaurante.child(mesa).addListenerForSingleValueEvent( new EscutadorFirebaseListar() );

            // Limpando os inputs
            txtMesa.setText(null);
            txtItem.setText(null);
            txtProduto.setText(null);
            txtPreco.setText(null);
        });


        // =====================
        // Botão Calcular preço
        // =====================
        btnCalcular.setOnClickListener(view -> {
            String mesa;
            mesa = txtMesa.getText().toString();
            restaurante.child(mesa).addListenerForSingleValueEvent( new EscutadorFirebaseCalcular() );

            // Limpando os inputs
            txtMesa.setText(null);
            txtItem.setText(null);
            txtProduto.setText(null);
            txtPreco.setText(null);
        });


        // =================
        // Botão Zerar Mesa
        // =================
        btnZerar.setOnClickListener(view -> {
            String mesa;
            mesa = txtMesa.getText().toString();
            restaurante.child(mesa).setValue(null);

            // Limpando os inputs
            txtMesa.setText(null);
            txtItem.setText(null);
            txtProduto.setText(null);
            txtPreco.setText(null);
        });


        // =====================
        // Botão atender pedido
        // =====================
        btnAtender.setOnClickListener(view -> {
            String mesa, item;
            mesa = txtMesaCoz.getText().toString();
            item = txtItemCoz.getText().toString();

            DatabaseReference i = restaurante.child(mesa).child(item);
            i.addListenerForSingleValueEvent( new EscutadorFirebaseAtender() );

//            // Limpando os inputs
//            txtMesaCoz.setText(null);
//            txtItemCoz.setText(null);
        });
    }


    // =====================================
    // Escutador Firebase para listar items
    // =====================================
    public class EscutadorFirebaseListar implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            // Testar se está vindo dados
            if (snapshot.exists()) {
                String produto, atendidoString = "";
                boolean atendido;
                double preco;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);

                    produto = item.getProduto();
                    preco = item.getPreco();
                    atendido = item.isAtendido();

                    if (!atendido) {
                        atendidoString = "Não";
                    } else {
                        atendidoString = "Sim";
                    }

                    Toast.makeText(MainActivity.this,
                            "Produto: " + produto + "\n" +
                            "Preço: " + preco + "\n" +
                            "Atendido: " + atendidoString,
                    Toast.LENGTH_SHORT).show();
                }
            }
        }

        // ==============================
        // Não trabalhar com esse método
        // ==============================
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    }


    // =======================================
    // Escutador Firebase para calcular preço
    // =======================================
    public class EscutadorFirebaseCalcular implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                double preco = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    preco += item.getPreco();
                }
                Toast.makeText(MainActivity.this,"Total da conta: " + preco,Toast.LENGTH_SHORT).show();
            }
        }

        // ==============================
        // Não trabalhar com esse método
        // ==============================
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    }


    // =======================================
    // Escutador Firebase para atender pedido
    // =======================================
    public class EscutadorFirebaseAtender implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            EditText txtMesaCoz = findViewById( R.id.txtMesaCoz );
            EditText txtItemCoz = findViewById( R.id.txtItemCoz );

            if (snapshot.exists()) {

                String mesa, item;

                mesa = txtMesaCoz.getText().toString();
                item = txtItemCoz.getText().toString();

                Item i = snapshot.getValue(Item.class);
                i.setAtendido(true);

                restaurante.child(mesa).child(item).setValue(i);

            }
        }

        // ==============================
        // Não trabalhar com esse método
        // ==============================
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    }
}