<?php

namespace Nativephp\Sensors;

use Illuminate\Support\ServiceProvider;

class SensorsServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton(Sensors::class, function () {
            return new Sensors();
        });
    }

    public function boot(): void
    {
        //
    }
}