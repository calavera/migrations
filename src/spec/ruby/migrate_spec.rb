require 'spec_helper'

describe Migrate do
  it "returns the migrations' path" do
    subject.send(:getMigrationsPath).should =~ %r{db/migrate$}
  end

  it "converts the java version to ruby version" do
    subject.send(:getRubyVersion, nil).should == 'nil'
    subject.send(:getRubyVersion, '001').should == '001'
  end

  it "loads the database configuration from the system" do
    set_property('migrations.db.name', 'foo')

    subject.send(:getDbConnection).should =~ /.+database => 'foo'.+/
  end

  it "raises an error if the database name is not set" do
    set_property('migrations.db.name', '')

    lambda {
      subject.send(:getDbConnection)
    }.should raise_error
  end

  it "uses jndi if exists the property" do
    set_property('migrations.db.jndi', 'fooDB/db')

    subject.send(:getDbConnection).should =~ %r{.+jndi => 'fooDB/db'}

    set_property('migrations.db.jndi', '')
  end

  it "uses host, dbname, user and password when jndi is not set" do
    set_property('migrations.db.host', '127.0.0.1')
    set_property('migrations.db.name', 'foo')
    set_property('migrations.db.user', 'bar')
    set_property('migrations.db.password', 'baz')

    connection = subject.send(:getDbConnection)
    connection.should =~ /.+database => 'foo'.+/
    connection.should =~ /.+host => '127.0.0.1'.+/
    connection.should =~ /.+username => 'bar'.+/
    connection.should =~ /.+password => 'baz'.+/
  end
end
