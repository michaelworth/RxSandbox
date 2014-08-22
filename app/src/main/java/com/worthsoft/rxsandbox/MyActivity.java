package com.worthsoft.rxsandbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;


public class MyActivity extends Activity {

    private final String data1[] = {"Line 1\n", "Line 2\n"};

    private TextView textView;

    private PublishSubject<Object> button1ClickStream = PublishSubject.create();
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        textView = (TextView) findViewById(R.id.textview);

        // User clicks on button1
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Drive an event on our button1ClickStream
                button1ClickStream.onNext(new Object());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


        final Observable<String> stringObservable = Observable.from(data1);

        subscription = Observable.zip(stringObservable, button1ClickStream, new Func2<String, Object, String>() {
            @Override
            public String call(String s, Object o) {
                return s;
            }
        })
                .startWith("Subscribed!\n")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView.append(s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        textView.append("onError\n");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        textView.append("onComplete\n");
                    }
                });
    }

    @Override
    protected void onStop() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
