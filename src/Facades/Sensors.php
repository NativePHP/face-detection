<?php

namespace Nativephp\Sensors\Facades;

use Illuminate\Support\Facades\Facade;

/**
 * @method static object|null accelerometer() Get accelerometer reading {x, y, z} in m/s²
 * @method static object|null gyroscope() Get gyroscope reading {x, y, z} in rad/s
 * @method static object|null magnetometer() Get magnetometer reading {x, y, z} in μT
 * @method static object|null deviceMotion() Get device motion {attitude, rotationRate, gravity}
 * @method static object|null barometer() Get barometer {pressure, altitude}
 * @method static object|null isAvailable() Check which sensors are available
 *
 * @see \Nativephp\Sensors\Sensors
 */
class Sensors extends Facade
{
    protected static function getFacadeAccessor(): string
    {
        return \Nativephp\Sensors\Sensors::class;
    }
}
