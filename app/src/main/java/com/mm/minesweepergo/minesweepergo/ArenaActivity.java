package com.mm.minesweepergo.minesweepergo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;
import com.mm.minesweepergo.minesweepergo.DomainModel.Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mm.minesweepergo.minesweepergo.R.id.aActiveGames;

public class ArenaActivity extends AppCompatActivity {

    public Arena arena;
    ArrayAdapter<String> adapterGames;
    ArrayList<String> gamesList;
    List<Game> allGames;

    ListView games;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.arena_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gamesList = new ArrayList<>();

        adapterGames = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,gamesList);

        games = (ListView) findViewById(R.id.aActiveGames);
        games.setAdapter(adapterGames);

        games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               return;
            }
        });

        arena = new Arena();

        Intent i = getIntent();
        Bundle bndl = i.getExtras();
        arena.name = bndl.getString("arenaName");
        arena.radius = bndl.getDouble("arenaRadius");
        arena.centerLat = bndl.getDouble("centerLat");
        arena.centerLon = bndl.getDouble("centerLon");

        ExecutorService transThread = Executors.newSingleThreadExecutor();
        transThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    allGames = HTTP.getArenaGames(arena.name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        transThread.shutdown();
        try {
            transThread.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.SECONDS);

        } catch (InterruptedException E) {
            // handle
        }

        for(int j=0; j<allGames.size(); j++)
        {
            gamesList.add(allGames.get(j).getId() + "\t\t" + allGames.get(j).getCreatorUsername());
        }
        adapterGames.notifyDataSetChanged();

        TextView name = (TextView) findViewById(R.id.aName);
        name.setText(arena.name);
        TextView area = (TextView) findViewById(R.id.aArea);
        area.setText(new DecimalFormat("#.##").format(arena.radius * arena.radius * Math.PI) + " m^2");
        TextView latLong = (TextView) findViewById(R.id.aCoords);
        latLong.setText(arena.centerLat+ " \t\t " + arena.centerLon);

    }
}
