require 'java'
require 'ant'
require 'fileutils'
include FileUtils
require 'rake/clean'

CLEAN.include '*.class', '*.jar'

#
# testing tasks
#

SPECS_PATH = 'src/spec/ruby'

require 'spec/rake/spectask'
Spec::Rake::SpecTask.new(:spec) do |spec|
  spec.libs << SPECS_PATH
  spec.spec_opts = ['--colour', '--format', 'specdoc']
  spec.spec_files = FileList["#{SPECS_PATH}/**/*_spec.rb"]
end

#
# java compilation tasks
#

TARGET_PATH = 'target'

namespace :ant do
  desc 'Clean the java target directory'
  task :clean do
    rm_f TARGET_PATH
  end

  desc 'Compile the java classes'
  task :compile => :clean do
    mkdir_p TARGET_PATH

    ant.javac({
      :srcdir => 'src/main/java',
      :destdir => TARGET_PATH
    })

    cp_r 'src/main/resources/db', TARGET_PATH
    cp_r 'src/main/resources/jruby', TARGET_PATH
  end

  task :jar => :compile do
    ant.jar :basedir => ".", :destfile => "migrations.jar" do
      fileset :dir => "build" do
          include :name => "**/*.class"
      end
      include :name => "src/main/resources/*"
      manifest do
        attribute :name => "Main-Class", :value => "migrations.Migrate"
      end
    end
  end
end

M2_REPO = '~/.m2/repository'
JRUBY_COMPLETE = "#{M2_REPO}/org/jruby/jruby-complete/1.5.3/jruby-complete-1.5.3.jar"

namespace :migrations do
  task :bundle do
    `mkdir -p src/main/resources/jruby/1.8`
    puts 'Installing Bundler'
    `java -jar #{JRUBY_COMPLETE} -S gem install bundler --no-ri --no-rdoc -i src/main/resources/jruby/1.8`
    puts 'Installing ActiveRecord'
    `java -jar #{JRUBY_COMPLETE} -S src/main/resources/jruby/1.8/bin/bundle install --path=src/main/resources`
  end
end
