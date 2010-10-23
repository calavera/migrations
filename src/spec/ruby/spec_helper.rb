begin
  require 'spec'
rescue LoadError
  require 'rubygems'
  gem 'rspec'
  require 'spec'
end

require 'java'

TARGET = File.expand_path('../../../target', File.dirname(__FILE__))
$CLASSPATH << TARGET

java_import 'migrations.Migrate'

def set_property(name, value)
  java.lang.System.set_property(name.to_s, value.to_s)
end
