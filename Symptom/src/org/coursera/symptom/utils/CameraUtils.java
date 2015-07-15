package org.coursera.symptom.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class CameraUtils{
	public static String TAG = "CameraUtils";
	public static final String JPEG_FILE_PREFIX = "IMG_";
	public static final String JPEG_FILE_SUFFIX = ".jpg";
	public static final String CONTENT_TYPE = "image/jpg";
	
	private String albumName = null;
	private String bitmapStorageKey = null;
	private String photoPathKey;
	private AlbumStorageDirFactory albumStorageDirFactory = null;
	private String currentPhotoPath;
	private String typePhoto;
	private boolean addPhotoGallery;
	private Bitmap imageBitmap;
	private Drawable defaultDrawable; //drawable por defecto que se cargara si no podemos cargar la foto
	// Use a WeakReference to ensure the ImageView can be garbage collected
	private WeakReference<ImageView> imageViewReference;
//	private ImageView imageViewReference;
	private boolean photoLoaded;
	private Context context;
	
	public CameraUtils(Context context, ImageView imageView, AlbumStorageDirFactory albumStorageDirFactory, String albumName){
		Log.d(TAG, "new CameraUtils called!!");
		this.setContext(context);
		this.setImageView(imageView);
		this.setAlbumName(albumName);
		this.setAlbumStorageDirFactory(albumStorageDirFactory);
	}
	
	public CameraUtils(Context context, AlbumStorageDirFactory albumStorageDirFactory, String albumName){
		Log.d(TAG, "new CameraUtils called!!");
		this.setContext(context);
		this.setAlbumName(albumName);
		this.setAlbumStorageDirFactory(albumStorageDirFactory);
	}
	
	public CameraUtils(Context context, ImageView imageView){
		this.setContext(context);
		this.setImageView(imageView);
	}
	
	/**
	 * 
	 * @return
	 */
	public static AlbumStorageDirFactory getAlgumFactory(){
		Log.d(TAG, "new CameraUtils called!!");
		AlbumStorageDirFactory mAlbumStorageDirFactory;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		return mAlbumStorageDirFactory;
	}
	
	/**
	 * Returns album path where photo will be saved
	 * @return a File object that represents path into file system
	 * @throws IOException
	 */
	public File getAlbumDir() throws IOException{
		Log.d(TAG, "getAlbumDir called!!");
		File storageDir = null;
		if (albumStorageDirFactory == null)
			throw new IOException("album storage dir factory is not created");
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = albumStorageDirFactory.getAlbumStorageDir(albumName);
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v(TAG, "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}
	
	/**
	 * Returns album path where photo will be saved
	 * 
	 * @param 
	 * @return a File object that represents path in the local file system
	 */
	public static File getAlbumDir(String albumName) throws IOException{
		Log.d(TAG, "getAlbumDir called!!");
		File storageDir = null;
		AlbumStorageDirFactory albumStorageDirFactory = getAlgumFactory();
		if (albumStorageDirFactory == null)
			throw new IOException("album storage dir factory is not created");
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = albumStorageDirFactory.getAlbumStorageDir(albumName);
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v(TAG, "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}

	/**
	 * Crea un fichero temporal para almacenar la imagen futura una vez tomada con la cámara
	 * @return Retorna el objeto File que apunta a dicha imagen futura en disco
	 * @throws IOException
	 * @throws IOException
	 */
	@SuppressLint("SimpleDateFormat")
	public File createImageFile() throws IOException {
		// Create an image file name
		Log.d(TAG, "createImageFile called!!");
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	/**
	 * Crea un fichero temporal para almacenar la imagen futura una vez tomada con la cámara y
	 * establece el path a dicho fichero almacenandolo en la propiedad currentPhotoPath
	 * @return Retorna el objeto File que apunta a dicha imagen futura en disco
	 * @throws IOException
	 */
	public File setUpPhotoFile() throws IOException {
		Log.d(TAG, "setUpPhotoFile called!!");
		File f = createImageFile();
		currentPhotoPath = f.getAbsolutePath();
		return f;
	}
	
	/**
	 * Carga la imagen desde el disco y la guarda en imageBitmap. Este bitmap cargado tendrá las dimensiones
	 * de la propiedad imageView pasada en la construcción del objeto 
	 */
	public void drawPhoto() {
		Log.d(TAG, "drawPhoto called!!");
		try {			
			//debemos controlar que mientas se ejecuta esto el usuario puede abandonar la actividad
			if (currentPhotoPath == null){
				Log.d(TAG, "currentPhotoPath is null. Return");
				return;
			}
			final ImageView imageView = imageViewReference.get();
			if (imageViewReference == null){ 
				Log.d(TAG, "imageViewReference is null. Return");
				return;
			}		
			int targetImageViewW = imageView.getWidth();
			if (targetImageViewW == 0){
				Log.d(TAG, "targetImageViewW is zero. Return");
				return;
			}
			int targetImageViewH = imageView.getHeight();
			if (targetImageViewH == 0){
				Log.d(TAG, "targetImageViewH is zero. Return");
				return;
			}
			//buscampos la orientacion con la que se ha realizado la foto
			int orientation = 0;
			int rotate = 0;
			try {
				ExifInterface exif = new ExifInterface(currentPhotoPath);
				orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
			    case ExifInterface.ORIENTATION_ROTATE_270:
			        rotate = 270;
			        break;
			    case ExifInterface.ORIENTATION_ROTATE_180:
			        rotate = 180;
			        break;
			    case ExifInterface.ORIENTATION_ROTATE_90:
			        rotate = 90;
			        break;
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "orientation:"+orientation);

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */
			/* Get the size of the image */		
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true; //con este parametro podemos averiguar el tamaño de la foto sin cargarla
			BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			Log.d(TAG, "photoW:"+photoW);
			Log.d(TAG, "photoH:"+photoH);
			
			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
//			bmOptions.inSampleSize = Math.min(photoW / targetImageViewW, photoH / targetImageViewH);
			bmOptions.inPurgeable = true;
			
			/* Decode the JPEG file into a Bitmap */
			Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
			int orientationScreen = context.getResources().getConfiguration().orientation;	
			int newW = bitmap.getWidth();
			int newH = bitmap.getHeight();
			Log.d(TAG,"photoW tras primer escalado:"+newW);
			Log.d(TAG,"photoH tras primer escalado:"+newH);
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, newW, newH, matrix, true);
			//bitmap.recycle();
			bitmap = null;
			Log.d(TAG,"Orientacion pantalla:"+orientationScreen);
			Log.d(TAG,"Orientacion foto:"+rotate);
			imageView.setImageBitmap(imageBitmap);
		} catch (Exception e) {
			//recycleBitmap();
			Log.e(TAG, "Error en el tratamiento de graficos", e);
		}		
	}

	/**
	 * Añade la imagen a la galeria de imagenes
	 */
	public void galleryAddPic() {
		Log.d(TAG, "galleryAddPic called!!");
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(currentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}
	
	/**
     * Copy the contents of an InputStream into an OutputStream.
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
	public static int copy(final InputStream in, final OutputStream out) throws IOException {
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        int totalRead = 0;
        int read = 0;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            totalRead += read;			
        }
        return totalRead;
    }
    
	/**
	 * Recicla el bitmap que contiene la imagen
	 */
	public void recycleBitmap() {
		Log.d(TAG, "recycleBitmap called!!");
		if (imageBitmap != null){
			imageBitmap.recycle();
			imageBitmap = null;
		}
	}    
	
	public void destroyResources() {
		Log.d(TAG, "destroyResources called!!");
		this.recycleBitmap();
		
	}

	public String getBitmapStorageKey() {
		return bitmapStorageKey;
	}

	public void setBitmapStorageKey(String bitmapStorageKey) {
		this.bitmapStorageKey = bitmapStorageKey;
	}
	
	public String getPhotoPathKey() {
		return photoPathKey;
	}

	public void setPhotoPathKey(String photoPathKey) {
		this.photoPathKey = photoPathKey;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public AlbumStorageDirFactory getAlbumStorageDirFactory() {
		return albumStorageDirFactory;
	}

	public void setAlbumStorageDirFactory(AlbumStorageDirFactory albumStorageDirFactory) {
		this.albumStorageDirFactory = albumStorageDirFactory;
	}

	public String getCurrentPhotoPath() {
		return currentPhotoPath;
	}

	public void setCurrentPhotoPath(String currentPhotoPath) {
		this.currentPhotoPath = currentPhotoPath;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public ImageView getImageView() {
		return imageViewReference.get();
	}

	public void setImageView(ImageView imageView) {
		this.imageViewReference = new WeakReference<ImageView>(imageView);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}


	public Drawable getDefaultDrawable() {
		return defaultDrawable;
	}
	

	public void setDefaultDrawable(Drawable drawable) {
		this.defaultDrawable = drawable;	
	}	


	public boolean isPhotoLoaded() {
		return photoLoaded;
	}

	public void setPhotoLoaded(boolean photoLoaded) {
		this.photoLoaded = photoLoaded;
	}

	public String getTypePhoto() {
		return typePhoto;
	}
	
	public boolean isAddPhotoGallery() {
		return addPhotoGallery;
	}

	public void setAddPhotoGallery(boolean addPhotoGallery) {
		this.addPhotoGallery = addPhotoGallery;
	}	

	public void setTypePhoto(String typePhoto) {
		this.typePhoto = typePhoto;
	}

}