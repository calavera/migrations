repositories.remote << 'http://repo1.maven.org/maven2'

JRUBY = 'org.jruby:jruby-complete:jar:1.5.3'
M2_REPO = '~/.m2/repository'
JRUBY_COMPLETE = "#{M2_REPO}/org/jruby/jruby-complete/1.5.3/jruby-complete-1.5.3.jar"

define 'migrations' do
  project.version = '0.1.0'
  compile.with JRUBY

  package(:jar)

  task :bundle do
    `mkdir -p src/main/resources/jruby/1.8`
    puts 'Installing Bundler'
    `java -jar #{JRUBY_COMPLETE} -S gem install bundler --no-ri --no-rdoc -i src/main/resources/jruby/1.8`
    puts 'Installing ActiveRecord'
    `java -jar #{JRUBY_COMPLETE} -S src/main/resources/jruby/1.8/bin/bundle install --path=src/main/resources`
  end
end
