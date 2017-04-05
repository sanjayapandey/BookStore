package com.example.sanjaya.bookstore;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

public class NextActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_next );

        final TextView tvView = (TextView) findViewById( R.id.tvView );
        Intent intent = getIntent( );
        String name = intent.getStringExtra( "name" );
        tvView.setText( "Welcome, " + name );
        final Button button = (Button) findViewById( R.id.home );
        button.setOnClickListener(
                new View.OnClickListener( ) {
                    public void onClick( View v ) {
                        Intent i = new Intent( NextActivity.this, MainActivity.class );
                        startActivity( i );
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_next, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        // noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }
        else if ( id == R.id.home ) {
            Intent i = new Intent( NextActivity.this, MainActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
}