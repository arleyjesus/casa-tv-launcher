package tv.casa.launcher;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

/** Created on demand and destroyed on exit. No native bridge or background playback. */
public final class GameBrowserActivity extends GameUi {
    private WebView browser;private FrameLayout root,custom;private LinearLayout layout;
    private View fullView;private WebChromeClient.CustomViewCallback fullCallback;private TextView address;
    private GameStore store;private boolean destroyed;
    public void onCreate(Bundle state){super.onCreate(state);scale=getResources().getDisplayMetrics().widthPixels/960f;fullscreen();store=new GameStore(this);root=new FrameLayout(this);root.setBackgroundColor(0xFF000000);layout=new LinearLayout(this);layout.setOrientation(1);root.addView(layout,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout toolbar=new LinearLayout(this);toolbar.setPadding(px(10),px(6),px(10),px(6));toolbar.setBackgroundColor(0xFF080E15);
        addTool(toolbar,"←",this::back,48);addTool(toolbar,"Sitios",this::sites,76);addTool(toolbar,"URL",this::enterUrl,60);addTool(toolbar,"☆",()->{String u=browser==null?null:browser.getUrl();if(GamePolicy.safeUrl(u)){store.remember("favorites",browser.getTitle(),u);Toast.makeText(this,"Favorito guardado",0).show();}},48);
        address=text("Cargando…",11,AuraTheme.MUTED);address.setSingleLine(true);address.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,-1,1);ap.leftMargin=px(10);toolbar.addView(address,ap);addTool(toolbar,"Pantalla",this::expand,100);addTool(toolbar,"Cerrar",this::finish,76);layout.addView(toolbar,new LinearLayout.LayoutParams(-1,px(49)));
        setContentView(root);
        try{browser=new WebView(this);WebSettings s=browser.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);s.setJavaScriptCanOpenWindowsAutomatically(false);s.setSupportMultipleWindows(false);s.setGeolocationEnabled(false);s.setMediaPlaybackRequiresUserGesture(true);s.setBuiltInZoomControls(true);s.setDisplayZoomControls(false);s.setLoadWithOverviewMode(true);s.setUseWideViewPort(true);s.setSafeBrowsingEnabled(true);CookieManager.getInstance().setAcceptThirdPartyCookies(browser,false);browser.setBackgroundColor(0xFF000000);
            browser.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){if(!GamePolicy.safeUrl(request.getUrl().toString())){if(request.isForMainFrame())Toast.makeText(GameBrowserActivity.this,"Solo se admiten páginas HTTPS",0).show();return true;}return false;}
                public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){String scheme=request.getUrl().getScheme();if("file".equalsIgnoreCase(scheme)||"content".equalsIgnoreCase(scheme))return new WebResourceResponse("text/plain","UTF-8",new java.io.ByteArrayInputStream(new byte[0]));return null;}
                public void onPageStarted(WebView view,String url,Bitmap icon){address.setText(Uri.parse(url).getHost());}
                public void onPageFinished(WebView view,String url){if(destroyed)return;address.setText(Uri.parse(url).getHost());if(GamePolicy.safeUrl(url))store.remember("recent",view.getTitle(),url);}
                public void onReceivedSslError(WebView view,SslErrorHandler h,SslError e){h.cancel();Toast.makeText(GameBrowserActivity.this,"No se pudo verificar la conexión del sitio",0).show();}
                public void onReceivedError(WebView view,WebResourceRequest req,WebResourceError err){if(req.isForMainFrame())address.setText("No se pudo cargar · revisa Internet o el WebView del TV");}
                public boolean onRenderProcessGone(WebView view,RenderProcessGoneDetail detail){release();Toast.makeText(GameBrowserActivity.this,"El navegador se cerró; vuelve a abrirlo",0).show();finish();return true;}
            });
            browser.setWebChromeClient(new WebChromeClient(){public void onPermissionRequest(PermissionRequest r){r.deny();}public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback cb){cb.invoke(origin,false,false);}public boolean onCreateWindow(WebView v,boolean dialog,boolean gesture,android.os.Message message){return false;}public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> cb,FileChooserParams params){cb.onReceiveValue(null);return true;}
                public void onShowCustomView(View view,CustomViewCallback callback){if(fullView!=null){callback.onCustomViewHidden();return;}fullView=view;fullCallback=callback;custom=new FrameLayout(GameBrowserActivity.this);custom.setBackgroundColor(0xFF000000);custom.addView(view,new FrameLayout.LayoutParams(-1,-1));root.addView(custom,new FrameLayout.LayoutParams(-1,-1));layout.setVisibility(View.GONE);view.requestFocus();fullscreen();}public void onHideCustomView(){hideCustom();}});
            browser.setDownloadListener((u,agent,disposition,mime,length)->Toast.makeText(this,"Las descargas no están habilitadas",0).show());layout.addView(browser,new LinearLayout.LayoutParams(-1,0,1));load(getIntent().getStringExtra("url"));browser.requestFocus();
        }catch(Exception e){release();TextView error=text("El componente web no está disponible. Actualiza Android System WebView desde Google Play.",18,AuraTheme.INK);layout.addView(error);}
    }
    private void addTool(LinearLayout row,String title,Runnable task,int width){TextView b=button(title,task);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(px(width),-1);lp.rightMargin=px(5);row.addView(b,lp);}
    private void load(String url){if(browser==null)return;if(!GamePolicy.safeUrl(url))url=GamePolicy.URLS[0];browser.loadUrl(url);}
    private void sites(){new AlertDialog.Builder(this).setTitle("Juegos en la web").setItems(GamePolicy.NAMES,(d,n)->load(GamePolicy.URLS[n])).show();}
    private void enterUrl(){EditText input=new EditText(this);input.setSingleLine(true);input.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_VARIATION_URI);input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(2048)});input.setHint("https://");new AlertDialog.Builder(this).setTitle("Abrir una página HTTPS").setView(input).setPositiveButton("Abrir",(d,w)->{String u=input.getText().toString().trim();if(GamePolicy.safeUrl(u))load(u);else Toast.makeText(this,"Introduce una dirección HTTPS válida",0).show();}).setNegativeButton("Cancelar",null).show();}
    private void expand(){if(browser==null)return;layout.getChildAt(0).setVisibility(View.GONE);browser.requestFocus();Toast.makeText(this,"Atrás vuelve a mostrar los controles",0).show();fullscreen();}
    private void hideCustom(){if(fullView==null)return;custom.removeAllViews();root.removeView(custom);custom=null;fullView=null;layout.setVisibility(View.VISIBLE);WebChromeClient.CustomViewCallback callback=fullCallback;fullCallback=null;if(callback!=null)callback.onCustomViewHidden();if(browser!=null)browser.requestFocus();fullscreen();}
    private void back(){if(fullView!=null){hideCustom();return;}if(layout.getChildAt(0).getVisibility()==View.GONE){layout.getChildAt(0).setVisibility(View.VISIBLE);layout.getChildAt(0).requestFocus();return;}if(browser!=null&&browser.canGoBack())browser.goBack();else finish();}
    public void onBackPressed(){back();}
    public void onResume(){super.onResume();if(browser!=null){browser.onResume();browser.resumeTimers();}fullscreen();}
    public void onPause(){if(browser!=null){browser.onPause();browser.pauseTimers();}super.onPause();}
    public void onStop(){super.onStop();if(!isChangingConfigurations())finish();}
    private void release(){if(destroyed)return;destroyed=true;hideCustom();if(browser!=null){browser.stopLoading();browser.setWebChromeClient(null);browser.setWebViewClient(new WebViewClient());android.view.ViewParent p=browser.getParent();if(p instanceof android.view.ViewGroup)((android.view.ViewGroup)p).removeView(browser);browser.destroy();browser=null;}}
    public void onDestroy(){release();super.onDestroy();}
}
