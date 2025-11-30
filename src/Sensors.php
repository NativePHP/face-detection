<?php

namespace Nativephp\Sensors;

class Sensors
{
    /**
     * Get current accelerometer reading
     *
     * @return object|null {x: float, y: float, z: float} in m/s²
     */
    public function accelerometer(): ?object
    {
        return $this->callBridge('Sensors.Accelerometer');
    }

    /**
     * Get current gyroscope reading
     *
     * @return object|null {x: float, y: float, z: float} in rad/s
     */
    public function gyroscope(): ?object
    {
        return $this->callBridge('Sensors.Gyroscope');
    }

    /**
     * Get current magnetometer reading
     *
     * @return object|null {x: float, y: float, z: float} in μT (microtesla)
     */
    public function magnetometer(): ?object
    {
        return $this->callBridge('Sensors.Magnetometer');
    }

    /**
     * Get current device motion data
     *
     * @return object|null {attitude: {pitch, roll, yaw}, rotationRate: {x, y, z}, gravity: {x, y, z}}
     */
    public function deviceMotion(): ?object
    {
        return $this->callBridge('Sensors.DeviceMotion');
    }

    /**
     * Get current barometer reading
     *
     * @return object|null {pressure: float (hPa), altitude: float (m)}
     */
    public function barometer(): ?object
    {
        return $this->callBridge('Sensors.Barometer');
    }

    /**
     * Check which sensors are available on this device
     *
     * @return object|null {accelerometer: bool, gyroscope: bool, magnetometer: bool, deviceMotion: bool, barometer: bool}
     */
    public function isAvailable(): ?object
    {
        return $this->callBridge('Sensors.IsAvailable');
    }

    /**
     * Call bridge function and return decoded data
     */
    protected function callBridge(string $function): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call($function, '{}');

            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data ?? null;
            }
        }

        return null;
    }
}
