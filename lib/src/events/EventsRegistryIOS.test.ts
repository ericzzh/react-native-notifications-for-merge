import { EventsRegistryIOS } from './EventsRegistryIOS';
import { CompletionCallbackWrapper } from '../adapters/CompletionCallbackWrapper';
import { NativeCommandsSender } from '../adapters/NativeCommandsSender';
import { NativeEventsReceiver } from '../adapters/NativeEventsReceiver';

jest.mock('../adapters/NativeCommandsSender')
jest.mock('../adapters/NativeEventsReceiver')

describe('EventsRegistryIOS', () => {
  let uut: EventsRegistryIOS;
  const mockNativeEventsReceiver = new NativeEventsReceiver() as jest.Mocked<NativeEventsReceiver>;
  const mockNativeCommandsSender = new NativeCommandsSender() as jest.Mocked<NativeCommandsSender>;
  const completionCallbackWrapper = new CompletionCallbackWrapper(mockNativeCommandsSender);

  beforeEach(() => {
    uut = new EventsRegistryIOS(mockNativeEventsReceiver, completionCallbackWrapper);
  });

  it('delegates registerPushKitRegistered to nativeEventsReceiver', () => {
    const cb = jest.fn();
    uut.registerPushKitRegistered(cb);
    expect(mockNativeEventsReceiver.registerPushKitRegistered).toHaveBeenCalledTimes(1);
    expect(mockNativeEventsReceiver.registerPushKitRegistered).toHaveBeenCalledWith(cb);
  });

  it('delegates registerPushKitNotificationReceived to nativeEventsReceiver', () => {
    const cb = jest.fn();
    uut.registerPushKitNotificationReceived(cb);

    expect(mockNativeEventsReceiver.registerPushKitNotificationReceived).toHaveBeenCalledTimes(1);
    expect(mockNativeEventsReceiver.registerPushKitNotificationReceived).toHaveBeenCalledWith(expect.any(Function));

  });

  it('should wrap callback with completion block', () => {
    const expectedNotification = { identifier: 'notificationId' }

    uut.registerPushKitNotificationReceived((notification) => {
      expect(notification).toEqual(expectedNotification);
    });

    const call = mockNativeEventsReceiver.registerPushKitNotificationReceived.mock.calls[0][0];

    call(expectedNotification);
  });

  it('should invoke finishPresentingNotification', () => {
    const expectedNotification = { identifier: 'notificationId' };

    uut.registerPushKitNotificationReceived((notification, completion) => {
      completion();

      expect(notification).toEqual(expectedNotification);

      expect(mockNativeCommandsSender.finishHandlingAction).toBeCalledWith('notificationId');
    });

    const call = mockNativeEventsReceiver.registerPushKitNotificationReceived.mock.calls[0][0];

    call(expectedNotification);
  });

  it('delegates appNotificationSettingsLinked to nativeEventsReceiver', () => {
    const cb = jest.fn();
    uut.appNotificationSettingsLinked(cb);
    expect(mockNativeEventsReceiver.appNotificationSettingsLinked).toHaveBeenCalledTimes(1);
    expect(mockNativeEventsReceiver.appNotificationSettingsLinked).toHaveBeenCalledWith(cb);
  });
});
