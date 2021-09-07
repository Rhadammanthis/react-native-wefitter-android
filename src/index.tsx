import { NativeModules } from 'react-native';

type WeFitterAndroidType = {
  configure(config: {
    token: string;
    apiUrl: string;
    notificationTitle?: string;
    notificationText?: string;
    notificationIcon?: string;
    notificationChannelId?: string;
    notificationChannelName?: string;
  }): void;
  connect(): void;
  disconnect(): void;
  isConnected(callback: (connected: boolean) => void): void;
  isSupported(callback: (supported: boolean) => void): void;
};

const { WeFitterAndroid } = NativeModules;

export default WeFitterAndroid as WeFitterAndroidType;
