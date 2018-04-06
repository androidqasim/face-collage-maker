package dauroi.photoeditor.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.api.response.StoreItem;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.config.DebugOptions;
import dauroi.photoeditor.utils.ProfileCache;
import dauroi.photoeditor.utils.StoreUtils;
import dauroi.photoeditor.vending.IabHelper;
import dauroi.photoeditor.vending.IabResult;
import dauroi.photoeditor.vending.Inventory;
import dauroi.photoeditor.vending.Purchase;

public class BaseStoreActivity extends BaseAdActivity {
	private static final String TAG = BaseStoreActivity.class.getSimpleName();
	// (arbitrary) request code for the purchase flow
	private static final int RC_REQUEST = 10001;
	// The helper object
	private IabHelper mHelper;
	private StoreItem mPurchasingItem;
	// Listener that's called when we finish querying the items and
	// subscriptions we own
	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			ALog.d(TAG, "Query inventory finished.");
			// Toast.makeText(BaseStoreActivity.this, "Query inventory
			// finished.", Toast.LENGTH_SHORT).show();
			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				// Toast.makeText(BaseStoreActivity.this, "Failed to query
				// inventory: " + result, Toast.LENGTH_SHORT)
				// .show();
				return;
			}

			ALog.d(TAG, "Query inventory was successful.");
			// Toast.makeText(BaseStoreActivity.this, "Query inventory was
			// successful.", Toast.LENGTH_SHORT).show();
			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */
			if (mPurchasingItem != null) {
				Purchase gasPurchase = inventory.getPurchase(mPurchasingItem.getBillingId());
				if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
					ALog.d(TAG, "We have gas. Consuming it.");
					// Toast.makeText(BaseStoreActivity.this, "We have gas.
					// Consuming it.", Toast.LENGTH_SHORT).show();
					mHelper.consumeAsync(inventory.getPurchase(mPurchasingItem.getBillingId()),
							mConsumeFinishedListener);
					return;
				}
			}

			updateUi();
			setWaitScreen(false);
			ALog.d(TAG, "Initial inventory query finished; enabling main UI.");
			// Toast.makeText(BaseStoreActivity.this, "Initial inventory query
			// finished; enabling main UI.", Toast.LENGTH_SHORT).show();
		}
	};

	// Callback for when a purchase is finished
	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			ALog.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				// complain("Error purchasing: " + result);
				// Toast.makeText(BaseStoreActivity.this, "Error purchasing: " +
				// result, Toast.LENGTH_SHORT).show();
				setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				// complain("Error purchasing. Authenticity verification
				// failed.");
				// Toast.makeText(BaseStoreActivity.this, "Error purchasing.
				// Authenticity verification failed.",
				// Toast.LENGTH_SHORT).show();
				setWaitScreen(false);
				return;
			}

			ALog.d(TAG, "Purchase successful.");
			// Toast.makeText(BaseStoreActivity.this, "Purchase successful.",
			// Toast.LENGTH_SHORT).show();
			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}
	};

	// Called when consumption is complete
	private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			ALog.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
			// Toast.makeText(BaseStoreActivity.this,
			// "Consumption finished. Purchase: " + purchase + ", result: " +
			// result, Toast.LENGTH_SHORT).show();
			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isSuccess()) {
				ALog.d(TAG, "Consumption successful. Provisioning.");
				// Toast.makeText(BaseStoreActivity.this, "Consumption
				// successful. Provisioning.", Toast.LENGTH_SHORT)
				// .show();
				savePurchasedData();
			} else {
				complain("Error while consuming: " + result);
			}
			updateUi();
			setWaitScreen(false);
			ALog.d(TAG, "End consumption flow.");
			// Toast.makeText(BaseStoreActivity.this, "End consumption flow.",
			// Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that
		 * you got from the Google Play developer console). This is not your
		 * developer public key, it's the *app-specific* public key.
		 *
		 * Instead of just storing the entire literal string here embedded in
		 * the program, construct the key at runtime from pieces or use bit
		 * manipulation (for example, XOR with some other string) to hide the
		 * actual key. The key itself is not secret information, but we don't
		 * want to make it easy for an attacker to replace the public key with
		 * one of their own and then fake messages from the server.
		 */
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp2CMBNDSG1D60tZRX5HUS/7KaaHcEcTpCNBQvMZjBNOrt/YI8Q51wNQYXRicCgjT8gNsxkR9hT0WgIblIhvT5yShu6ACwBKPLZaMV9ioUjzKudFYxlhVRDmbuzmpbBwVHNtTUmHxbK6RgoXh5zTStdRzw5Ith5xM0ONQPWdqqsUPFmTIm69e2537x8V8ogAhKMziAOpe35+YAEJ/CxPVZgxeDbPXvZTzoxg76jB6JGtiHaCcJWNLZZUavHXz/aYKRkK1oLN/ps8kHjtUXJJ/2btiGdFom6k2wsHdgCGqzFfY1HPerl2erYOr+DppNMbScgEJpRcRe4SW6HuvdPuOcQIDAQAB";

		// Some sanity checks to see if the developer (that's you!) really
		// followed the
		// instructions to run this sample (don't put these checks on your app!)
		if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
			throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
		}
		if (getPackageName().startsWith("com.example")) {
			throw new RuntimeException("Please change the sample's package name! See README.");
		}

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		ALog.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(DebugOptions.ENABLE_DEBUG);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		ALog.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					// complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				ALog.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// very important:
		ALog.d(TAG, "Destroying helper.");
		try {
			if (mHelper != null) {
				mHelper.dispose();
				mHelper = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ALog.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			ALog.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	/** Verifies the developer payload of a purchase. */
	private boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		ALog.d(TAG, "verifyDeveloperPayload, payload=" + payload);
		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 *
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */
		if ("hB9/g42auxbsG6HNY+/SQQ==".equals(payload)) {
			return true;
		} else {
			return false;
		}
	}

	private void savePurchasedData() {
		/*
		 * WARNING: on a real application, we recommend you save data in a
		 * secure way to prevent tampering. For simplicity in this sample, we
		 * simply store the data using a SharedPreferences.
		 */
		StoreUtils.setPurchasedDevice();
		if (mPurchasingItem != null) {
			StoreUtils.downloadItem(mPurchasingItem);
		}
	}

	// updates UI to reflect model
	public void updateUi() {
		// update the car color to reflect premium status or lack thereof
		// ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium
		// ? R.drawable.premium : R.drawable.free);
		//
		// // "Upgrade" button is only visible if the user is not premium
		// findViewById(R.id.upgrade_button).setVisibility(mIsPremium ?
		// View.GONE : View.VISIBLE);
		//
		// // "Get infinite gas" button is only visible if the user is not
		// subscribed yet
		// findViewById(R.id.infinite_gas_button).setVisibility(mSubscribedToInfiniteGas
		// ?
		// View.GONE : View.VISIBLE);
		//
		// // update gas gauge to reflect tank status
		// if (mSubscribedToInfiniteGas) {
		// ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
		// }
		// else {
		// int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 :
		// mTank;
		// ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
		// }
	}

	// Enables or disables the "please wait" screen.
	private void setWaitScreen(boolean set) {
		// TODO:
		// findViewById(R.id.screen_main).setVisibility(set ? View.GONE :
		// View.VISIBLE);
		// findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE :
		// View.GONE);
	}

	private void complain(String message) {
		ALog.e(TAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}

	private void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		ALog.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	public void purchaseItem(final StoreItem item) {
		if (item.getPrice() > 0) {
			setWaitScreen(true);
			/*
			 * TODO: for security, generate your payload here for verification.
			 * See the comments on verifyDeveloperPayload() for more info. Since
			 * this is a SAMPLE, we just use an empty string, but on a
			 * production app you should carefully generate this.
			 */
			mPurchasingItem = item;
			final String payload = "hB9/g42auxbsG6HNY+/SQQ==";
			try {
				mHelper.launchPurchaseFlow(this, mPurchasingItem.getBillingId(), RC_REQUEST, mPurchaseFinishedListener,
						payload);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
//			if (DebugOptions.ENABLE_DEBUG) {
//				StoreUtils.downloadItem(item);
//			}
		} else if (item.getPermission().equalsIgnoreCase(StoreItem.VIP_PERMISSION)) {
			final String token = ProfileCache.getToken(PhotoEditorApp.getAppContext());
			if (token == null || token.length() < 1) {
				// Show login screen
			} else {
				StoreUtils.downloadItem(item);
			}
		} else {
			StoreUtils.downloadItem(item);
		}
	}

}
