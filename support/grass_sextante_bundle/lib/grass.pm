############################################################################
#
# MODULE:	grass.pm
# AUTHOR(S):	Jachym Cepicky jachym.cepicky [at] centrum [dot] cz
# PURPOSE:	Perlmodule for GRASS-Perl scripting
# COPYRIGHT:	(C) 2005 by Jachym Cepicky
#
#		This program is free software under the GNU General Public
#		License (>=v2). Read the file COPYING that comes with GRASS
#		for details.
#
#############################################################################


package GRASS;

### is GRASS running?
unless($ENV{GISBASE}){

    print "\n\tYou have to run GRASS first!\n\n";
}

#
# creates new object 
# 
sub new 
{
    my ($classname, $description) = @_;
    my $self = {};
    
    $self->{'description'} = $description;
    return bless $self, $classname;
    
}

#
# loads arguments. if one of them is "help", calls &print_help()
# 
sub LoadArgs 
{
    
    my ($self, $arg_r) = @_;  ## arguments, which can be found
    my %args = ();      ## this hash will be returned back

    ## foreach argument from command line
    foreach my $ARGV (@main::ARGV) {
        
        ## help required?
        if ($ARGV =~ m/(help)|(^-h$)/i) {

            &print_help($self->{description},$arg_r);
        }

        ## store arguments
        else {
            foreach my $arg (keys %$arg_r) {

                ## command line parameter is expected
                if ($ARGV =~ m/$arg/) {
                    $ARGV =~ s/$arg=//;

                    ## if it is a flag
                    if ($ARGV =~ m/^-[a-zA-Z]/) {
                        $ARGV = 1;
                    }

                    ## store it
                    $args{$arg} = $ARGV;
                    ### print $args{$arg};
                }
            }
        }
    }
    
    ## controling, if all vars are set
    foreach $arg (keys %$arg_r) {
        
        ### flags
        if ($arg =~ m/-[a-zA-Z]/) {
            if (!$args{$arg}) {
                $args{$arg} = 0;
            }
        }

        ### arguments
        else {
            if (!$args{$arg} && 
                ($arg_r->{$arg}->{'required'} == 1 || 
                 $arg_r->{$arg}->{'required'} eq 'yes' )) {
                    
                print STDERR "ERROR: Argument '$arg' not set!\n";
                &print_help($self->{description},$arg_r);
                $args{$arg} = "";
            }
        }
 
    }
    
    
    bless \%args, 'GRASS';
    
    return (\%args);
}

#
# prints help to STDOUT end exits.
# the Help message is construnted from $arg_r variable
#  
# example: {'input'=>{'type'=>'raster',
#                     'description'=>'Vstupní rastr',
#                     'required'=>1},
#           'output'=>{'type'=>'raster',
#                      'description'=>'Výstupní rastr',
#                      'required'=>0},
#           '-o'=>{'description'=>'Pøepsat výstupní rastr? (Výchozí: 0)'}
#             }
#
sub print_help 
{
    my ($description,$arg_r) = @_;

    my $module_name = $0;       # file name
    $module_name =~ s/^(\/.+\/)*(.+)$/$2/;

    my $help_str = "$module_name "; # help string

    ### DESCRIPTION
    $help_str =~ s/^/\nDescription:\n $description\n\nUsage:\n /;
    
    ### USAGE
    ### flags into USAGE
    foreach $arg (keys %$arg_r) {

        ### -o
        if ($arg =~ m/-[a-zA-Z]/) { 

            $help_str .= "$arg ";
        }
    }
 
    ### arguments into USAGE
    foreach my $arg (keys %$arg_r) {

        ### input=
        unless ($arg =~ m/-[a-zA-Z]/) {

            ## [input=raster] or input=raster
            if ($arg_r->{$arg}->{'required'} && 
                ($arg_r->{$arg}->{'required'} == 1 ||
                 $arg_r->{$arg}->{'required'} eq 'yes')) {
                    
                $help_str .= "$arg=$arg_r->{$arg}->{'type'} ";
            }
            else {
                $help_str .= "[$arg=$arg_r->{$arg}->{'type'}] ";
            }

            
        }
    }

   
    ### WHERE
    $help_str .= "\n\nWhere:\n";

    ### flags into WHERE
    foreach  $arg (keys %$arg_r) {

        ### -o
        if ($arg =~ m/-[a-zA-Z]/) {
            $help_str .= "   $arg\t $arg_r->{$arg}->{'description'}\n";
        }
        
    }

    ### arguments into WHERE
     foreach  $arg (keys %$arg_r) {

        ### input=
        unless ($arg =~ m/-[a-zA-Z]/) {
           $help_str .= "   $arg\t $arg_r->{$arg}->{'description'}\n";
        }
        
    }
    
    $help_str .= "\n";
    
    ### that's all fokls
    print $help_str;
    exit;
}

#
# loads GRASS env variables
# 
sub Gisenv 
{
    my $self = shift;
    my $gisenv = {};
    my ($name, $var);
    
    ### foreach g.gisenv separate them to hash
    foreach (`g.gisenv`) {
        chomp;
        s/'|;//g; ### without ' and ;
        ($name,$var) = split(m/=/,$_);
        $gisenv->{$name} = $var;
    }
    
    return $gisenv;
}

return 1;
__END__
###############################################################################
#POD
=head1 NAME

grass - Module for dealing with GIS GRASS environment

=head1 SYNOPSIS

  use grass;
  
  # new object with your script description
  my $grass = new GRASS ("Just some script for trying");

  # what arguments and flags should the script get 
  # + what should appear in the
  # help message
  my $arg = $grass->LoadArgs({'input'=>{'type'=>'raster',
                                      'description'=>'Input raster',
                                      'required'=>1},
                       'output'=>{'type'=>'raster',
                                  'description'=>'Output raster',
                                  'required'=>'no'},
                       '-o'=>{'description'=>'Overwrite output raster?'}
                      });
  
  # some grass enviroment variables (just reading)
  my $gisenv = $grass->Gisenv();

  #
  # using the variables
  # 
  print $arg->{'input'},"\n",
        $arg->{'-o'},"\n";

  print $gisenv->{LOCATION_NAME},"\n";

