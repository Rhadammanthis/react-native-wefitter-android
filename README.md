# react-native-wefitter-android

React Native library for WeFitter and Android

## Installation

```sh
yarn add git://github.com/ThunderbyteAI/react-native-wefitter-android.git#v0.0.1
```

## Usage

Add the following code and change `YOUR_TOKEN` and `YOUR_API_URL`:

```ts
import WeFitterAndroid from 'react-native-wefitter-android';

// ...

const [connected, setConnected] = useState<boolean>(false);
const [supported, setSupported] = useState<boolean>(false);

useEffect(() => {
  if (Platform.OS === 'android') {
    WeFitterAndroid.isSupported((supported: boolean) =>
      setSupported(supported)
    );
  }
}, []);

useEffect(() => {
  if (supported) {
    // create native event emitter and event listeners to handle status updates
    const emitter = new NativeEventEmitter();
    const configuredListener = emitter.addListener(
      'onConfiguredWeFitterAndroid',
      (event: { configured: boolean }) =>
        console.log(`WeFitterAndroid configured: ${event.configured}`)
    );
    const connectedListener = emitter.addListener(
      'onConnectedWeFitterAndroid',
      (event: { connected: boolean }) => {
        console.log(`WeFitterAndroid connected: ${event.connected}`);
        setConnected(event.connected);
      }
    );
    const errorListener = emitter.addListener(
      'onErrorWeFitterAndroid',
      (event: { error: string }) => {
        console.log(`WeFitterAndroid error: ${event.error}`);
      }
    );

    // create config
    const config = {
      token: 'YOUR_TOKEN', // required
      apiUrl: 'YOUR_API_URL', // required
      notificationTitle: 'CUSTOM_TITLE', // optional
      notificationText: 'CUSTOM_TEXT', // optional
      notificationIcon: 'CUSTOM_ICON', // optional, e.g. `ic_notification` placed in either drawable, mipmap or raw
      notificationChannelId: 'CUSTOM_CHANNEL_ID', // optional
      notificationChannelName: 'CUSTOM_CHANNEL_NAME', // optional
    };

    // configure WeFitterAndroid
    WeFitterAndroid.configure(config);

    return () => {
      configuredListener.remove();
      connectedListener.remove();
      errorListener.remove();
    };
  }
  return;
}, [supported]);

const onPressConnectOrDisconnect = () => {
  if (Platform.OS === 'android') {
    if (supported) {
      connected ? WeFitterAndroid.disconnect() : checkPermissionAndConnect();
    } else {
      Alert.alert(
        'Not supported',
        'This device does not have a sensor to count steps'
      );
    }
  } else {
    Alert.alert('Not supported', 'WeFitterAndroid is not supported on iOS');
  }
};

const checkPermissionAndConnect = async () => {
  // On 29+ a runtime permission needs to be requested before connecting
  if (Platform.Version >= 29) {
    const granted = await PermissionsAndroid.request(
      'android.permission.ACTIVITY_RECOGNITION' as Permission
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      // connect if requested permission has been granted
      WeFitterAndroid.connect();
    }
  } else {
    WeFitterAndroid.connect();
  }
};
```

See the [example](example/src/App.tsx) for the full source.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.
