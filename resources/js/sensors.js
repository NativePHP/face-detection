/**
 * NativePHP Sensors Plugin - JavaScript Library
 * Provides easy access to device sensors from JavaScript/TypeScript
 *
 * @example
 * import { sensors } from '@nativephp/sensors';
 *
 * // Check available sensors
 * const available = await sensors.isAvailable();
 *
 * // Start accelerometer
 * await sensors.accelerometer.start();
 *
 * // Read values (call in a setInterval)
 * const data = await sensors.accelerometer.read();
 * console.log(data); // { x: 0.1, y: 9.8, z: 0.3 }
 *
 * // Stop when done
 * await sensors.accelerometer.stop();
 */

const baseUrl = '/_native/api/call';

/**
 * Internal bridge call function
 * @private
 */
async function bridgeCall(method, params = {}) {
    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]')?.content || ''
        },
        body: JSON.stringify({ method, params })
    });

    const result = await response.json();

    if (result.status === 'error') {
        throw new Error(result.message || 'Native call failed');
    }

    // Native response is wrapped twice: PHP wraps native's {status, data} response
    const nativeResponse = result.data;
    if (nativeResponse && nativeResponse.data !== undefined) {
        return nativeResponse.data;
    }

    return nativeResponse;
}

/**
 * Accelerometer sensor
 * Returns acceleration in m/s² for x, y, z axes
 */
export const accelerometer = {
    start: () => bridgeCall('Sensors.Accelerometer.Start'),
    stop: () => bridgeCall('Sensors.Accelerometer.Stop'),
    read: () => bridgeCall('Sensors.Accelerometer.Read')
};

/**
 * Gyroscope sensor
 * Returns rotation rate in rad/s for x, y, z axes
 */
export const gyroscope = {
    start: () => bridgeCall('Sensors.Gyroscope.Start'),
    stop: () => bridgeCall('Sensors.Gyroscope.Stop'),
    read: () => bridgeCall('Sensors.Gyroscope.Read')
};

/**
 * Magnetometer sensor
 * Returns magnetic field in μT for x, y, z axes
 */
export const magnetometer = {
    start: () => bridgeCall('Sensors.Magnetometer.Start'),
    stop: () => bridgeCall('Sensors.Magnetometer.Stop'),
    read: () => bridgeCall('Sensors.Magnetometer.Read')
};

/**
 * Device motion sensor
 * Returns attitude (pitch, roll, yaw), rotationRate (x, y, z), gravity (x, y, z)
 */
export const deviceMotion = {
    start: () => bridgeCall('Sensors.DeviceMotion.Start'),
    stop: () => bridgeCall('Sensors.DeviceMotion.Stop'),
    read: () => bridgeCall('Sensors.DeviceMotion.Read')
};

/**
 * Barometer sensor
 * Returns pressure in hPa and relative altitude in meters
 */
export const barometer = {
    start: () => bridgeCall('Sensors.Barometer.Start'),
    stop: () => bridgeCall('Sensors.Barometer.Stop'),
    read: () => bridgeCall('Sensors.Barometer.Read')
};

/**
 * Check which sensors are available on this device
 * @returns {Promise<{accelerometer: boolean, gyroscope: boolean, magnetometer: boolean, deviceMotion: boolean, barometer: boolean}>}
 */
export async function isAvailable() {
    return bridgeCall('Sensors.IsAvailable');
}

/**
 * Sensors namespace object
 */
export const sensors = {
    accelerometer,
    gyroscope,
    magnetometer,
    deviceMotion,
    barometer,
    isAvailable
};

export default sensors;
